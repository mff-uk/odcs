package cz.cuni.xrg.intlib.commons.app.module.osgi;

import java.io.File;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfig;

/**
 * OSGI based implementation of {@link ModuleFacade}.
 * 
 * @author Petyr
 *
 */
class OSGIModuleFacade implements ModuleFacade {

	/**
	 * Logger class.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(OSGIModuleFacade.class);

	/**
	 * OSGi framework class.
	 */
	private org.osgi.framework.launch.Framework framework = null;

	/**
	 * OSGi context.
	 */
	private org.osgi.framework.BundleContext context = null;

	/**
	 * Store loaded bundles. DPU's bundles are stored under their directory
	 * name. Libraries under their uri.
	 */
	private ConcurrentHashMap<String, BundleContainer> loadedBundles = new ConcurrentHashMap<>();

	@Autowired
	private ModuleFacadeConfig configuration;

	/**
	 * Start the OSGI framework.
	 */
	public void start() throws FrameworkStartFailedException {
		FrameworkFactory frameworkFactory = null; // org.eclipse.osgi.launch.EquinoxFactory

		try {
			frameworkFactory = java.util.ServiceLoader
					.load(FrameworkFactory.class).iterator().next();
		} catch (Exception ex) {
			LOG.error("Failed to load osgi framework class.", ex);
			// failed to load FrameworkFactory class
			throw new FrameworkStartFailedException(
					"Can't load class FrameworkFactory.", ex);
		}

		framework = frameworkFactory.newFramework(prepareSettings());
		context = null;
		try {
			// start OSGi container ..
			framework.start();
		} catch (org.osgi.framework.BundleException ex) {
			LOG.error("Failed to start OSGI framework.", ex);
			// failed to start/initiate framework
			throw new FrameworkStartFailedException(
					"Failed to start OSGi framework.", ex);
		}

		try {
			context = framework.getBundleContext();
		} catch (SecurityException ex) {
			LOG.error("Failed to get osgi context.", ex);
			// we have to stop framework ..
			stop();
			throw new FrameworkStartFailedException(
					"Failed to get OSGi context.", ex);
		}
	}

	/**
	 * Stop OSGI and uninstall all bundles.
	 */
	public void stop() {
		for (BundleContainer bundle : loadedBundles.values()) {
			try {
				bundle.uninstall();
			} catch (BundleException e) {
				LOG.error("Failed to uninstall bundle {}", bundle.getUri(), e);
			}
		}
		loadedBundles.clear();
		try {
			if (framework != null) {
				// stop equinox
				framework.stop();
			}
		} catch (Exception e) {
			// we can't throw here ..
		} finally {
			framework = null;
			context = null;
		}
	}

	@Override
	public Object getInstance(DPUTemplateRecord dpu) throws ModuleException {
		BundleContainer container = install(dpu);
		String fullMainClassName = container.getMainClassName();
		// load and return
		return container.loadClass(fullMainClassName);
	}

	@Override
	public void unLoad(DPUTemplateRecord dpu) {
		final String jarDirectory = dpu.getJarDirectory();
		BundleContainer container = loadedBundles.get(jarDirectory);
		if (container == null) {
			// nothing to uninstall
			return;
		}
		try {
			container.uninstall();
		} catch (BundleException e) {
			LOG.error("Failed to uninstall bundle in: {}", jarDirectory, e);
		}
		loadedBundles.remove(jarDirectory);
	}

	@Override
	public boolean beginUpdate(String directory) {
		BundleContainer container = loadedBundles.get(directory);
		if (container == null) {
			// bundle is not loaded yet ..
			return false;
		}		
		container.beginUpdate();
		return true;
	}

	@Override
	public void update(String directory, String newName) throws ModuleException {
		BundleContainer container = loadedBundles.get(directory);
		if (container == null) {
			// bundle is not loaded yet ..
		} else {
			// we need to construct new uri
			StringBuilder uri = new StringBuilder();
			uri.append("file:///");
			uri.append(directory);
			uri.append(File.separator);
			uri.append(newName);			
			
			try {
				container.update(context, uri.toString());
			} catch (BundleException e) {
				throw new ModuleException("Failed to update bundle.", e);
			}
		}
	}

	@Override
	public void endUpdate(String directory) {
		BundleContainer container = loadedBundles.get(directory);
		if (container == null) {
			// bundle is not loaded yet ..
		} else {
			container.endUpdate();
		}
	}

	@Override
	public Dictionary<String, String> getJarProperties(DPUTemplateRecord dpu) {
		try {
			BundleContainer container = install(dpu);
			return container.getHeaders();
		} catch (ModuleException e) {
			LOG.error("Failed to install bundle: {}", dpu.getJarDirectory(), e);
		}
		return null;
	}

	@Override
	public void loadLibs(List<String> directoryPaths) {
		for (String directory : directoryPaths) {
			loadLibs(directory);
		}
	}

	/**
	 * Load jar files from given directory as libraries.
	 * @param directoryPath
	 */
	private void loadLibs(String directoryPath) {
		LOG.info("Loading libraries from: {}", directoryPath);

		File directory = new File(directoryPath);
		File[] fList = directory.listFiles();
		if (fList == null) {
			LOG.error("Wrong directory path: {}", directoryPath);
			return;
		}
		for (File file : fList) {
			if (file.isFile()) {
				if (file.getName().contains("jar")) {
					LOG.debug("Loading lirbary: {}", file.toString());
					// now we need uri
					String uri = "file:///"
							+ file.getAbsolutePath().replace('\\', '/');
					installLib(uri);
				}
			}
		}
	}
	
	/**
	 * Return configuration used to start up OSGi implementation.
	 * 
	 * @return
	 */
	private java.util.Map<String, String> prepareSettings() {
		java.util.Map<String, String> config = new java.util.HashMap<>();
		config.put("osgi.console", "");
		config.put("osgi.clean", "true");
		config.put("osgi.noShutdown", "true");
		// export packages
		config.put(org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				configuration.getPackagesToExpose());
		return config;
	}

	/**
	 * If container is not installed yet then install it. In every case return
	 * the installed instance of {@link BundleContainer} for given
	 * directory. The container is installed as DPU container.
	 * 
	 * @param dpu
	 * @return
	 * @throw ModuleException
	 */	
	private BundleContainer install(DPUTemplateRecord dpu) 
			throws ModuleException{
		return install(dpu.getJarDirectory(), dpu.getJarName());
	}
	
	/**
	 * If container is not installed yet then install it. In every case return
	 * the installed instance of {@link BundleContainer} for given
	 * directory. The container is installed as DPU container.
	 * 
	 * @param directory DPU's directory.
	 * @param fileName
	 * @return
	 * @throw ModuleException
	 */
	private BundleContainer install(String directory, String fileName)
			throws ModuleException {
		if (loadedBundles.contains(directory)) {
			return loadedBundles.get(directory);
		} // else we load .. we need uri
		StringBuilder uri = new StringBuilder();
		uri.append("file:///");
		uri.append(configuration.getDpuFolder());
		uri.append(File.separator);
		uri.append(fileName);

		Bundle bundle = null;
		try {
			bundle = context.installBundle(uri.toString());
		} catch (org.osgi.framework.BundleException e) {
			throw new ModuleException(e);
		}

		BundleContainer bundleContainer = null;
		bundleContainer = new BundleContainer(bundle, uri.toString(),
				directory);
		// add bundle to the loaded bundle list and reverse list
		loadedBundles.put(directory, bundleContainer);

		return bundleContainer;
	}

	/**
	 * Try to load library. In case of error log but do not throw. If succed
	 * the newly loaded library is stored in {@link #loadedBundles} and returned.
	 * 
	 * @param uri Bundles uri.
	 * @return Null in case of error.
	 */
	private BundleContainer installLib(String uri) {
		Bundle bundle = null;
		try {
			bundle = context.installBundle(uri);
		} catch (org.osgi.framework.BundleException e) {
			LOG.error("Failed to load libary from: {}", uri, e);
			return null;
		}

		BundleContainer bundleContainer = null;
		bundleContainer = new BundleContainer(bundle, uri.toString());
		loadedBundles.put(uri, bundleContainer);
		
		return bundleContainer;
	}

}
