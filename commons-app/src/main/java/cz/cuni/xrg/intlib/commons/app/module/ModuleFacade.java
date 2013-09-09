package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Dictionary;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade providing actions with DPURecord module implementations.
 * 
 * @author Petyr
 * 
 */
public class ModuleFacade {

	/**
	 * Used framework.
	 */
	private OSGIFramework framework;

	/**
	 * Facade configuration.
	 */
	private ModuleFacadeConfig configuration;

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ModuleFacade.class);

	/**
	 * Base ctor. The configuration is not used until some other method is
	 * called. So is not necessary to have all configuration fully set when
	 * passing to the ctor.
	 * 
	 * @param configuration
	 */
	public ModuleFacade(ModuleFacadeConfig configuration) {
		this.framework = new OSGIFramework();
		this.configuration = configuration;
	}

	/**
	 * Start framework. Must be called as a first method after ctor.
	 * 
	 * @throws FrameworkStartFailedException
	 * @throws LibsLoadFailedException
	 */
	public void start()
			throws FrameworkStartFailedException,
				LibsLoadFailedException {
		LOG.info("Starting module facade");
		// start
		framework.start(configuration.getPackagesToExpose());
		// load libraries
		try {
			installDirectories(configuration.getDpuLibFolder());
		} catch (Exception e) {
			LOG.error("installDirectory failed", e);
			throw new LibsLoadFailedException(e);
		}
	}

	/**
	 * Stop framework. Should be called as last method after releasing all used
	 * instances from ModuleFacade.
	 */
	public void stop() {
		LOG.info("Stopping OSGI framework ...");
		framework.stop();
	}

	/**
	 * Create URL to the file in DPU's directory.
	 * @param relativePath
	 * @return
	 */
	private String createURI(String relativePath) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("file:///");
		strBuilder.append(configuration.getDpuFolder());
		strBuilder.append(File.separator);
		strBuilder.append(relativePath);
		return strBuilder.toString();
	}

	/**
	 * Load main class from bundle and return it as object.
	 * 
	 * @param relativePath Relative path in DPU's directory.
	 * @return Loaded class.
	 * @throws FileNotFoundException
	 * @throws ClassLoadFailedException
	 * @throws BundleInstallFailedException
	 */
	public Object getObject(String relativePath)
			throws BundleInstallFailedException,
				ClassLoadFailedException,
				FileNotFoundException {
		checkExistance(relativePath); // throw FileNotFoundException
		final String uri = createURI(relativePath);
		// throw BundleInstallFailedException
		return framework.loadClass(uri);
	}

	/**
	 * Uninstall bundle from system, use with caution. No instance of any class
	 * from bundle should exist.
	 * 
	 * @param relativePath
	 */
	public void uninstall(String relativePath) {
		final String uri = createURI(relativePath);
		BundleContainer container = framework.getBundle(uri);
		if (container != null) {
			// we have something to uninstall
			framework.uninstallBundle(container);
		} else {
			// bundle has not been installed
		}
	}

	/**
	 * Return class loader for bundle.
	 * 
	 * @param relativePath RelativePath Relative path in DPU's directory.
	 * @return Bundle's ClassLoader
	 * @throws FileNotFoundException
	 * @throws BundleInstallFailedException
	 */
	public ClassLoader getClassLoader(String relativePath)
			throws BundleInstallFailedException,
				FileNotFoundException {
		checkExistance(relativePath); // throw FileNotFoundException
		final String uri = createURI(relativePath);
		// throw BundleInstallFailedException
		BundleContainer container = framework.installBundle(uri);
		// get class loader
		return container.getClassLoader();
	}

	/**
	 * Return content of manifest file (properties) for given jar-file that is
	 * stored in DPU's directory. This method does load jar-file into system.
	 * To unload jar-file use {@link #uninstall(String)} 
	 * 
	 * @param relativePath Relative path in DPU's directory.
	 * @return Jar attributes or null in case of error.
	 */
	public Dictionary<String, String> getJarProperties(String relativePath) {
		try {
			checkExistance(relativePath);
		} catch (FileNotFoundException e) {
			return null;
		}
		final String uri = createURI(relativePath);
		
		BundleContainer container;
		try {
			container = framework.installBundle(uri);
		} catch (BundleInstallFailedException e) {
			return null;
		}
		return container.getHeaders();
	}

	/**
	 * Check if the bundle exist.
	 * 
	 * @param relativePath RelativePath Relative path in DPU's directory.
	 * @throws FileNotFoundException
	 */
	private void checkExistance(String relativePath)
			throws FileNotFoundException {
		File file = new File(configuration.getDpuFolder() + File.separator
				+ relativePath);
		if (file.exists()) {
			// file exist ..
		} else {
			throw new FileNotFoundException("File '" + file.getAbsolutePath()
					+ "' does not exist.");
		}
	}

	/**
	 * Load files in given directories (non-recursive). If the file is *.jar
	 * then load id as a bundle.
	 * 
	 * @param directoryPath system path to directory. Not prefixed by file:///
	 * @throws LibsLoadFailedException
	 */
	private void installDirectories(List<String> directoryPaths)
			throws LibsLoadFailedException {
		for (String directory : directoryPaths) {
			installDirectory(directory);
		}
	}

	/**
	 * List files in single directory (non-recursive). If the file is *.jar then
	 * load id as a bundle.
	 * 
	 * @param directoryPath system path to directory. Not prefixed by file:///
	 * @throws LibsLoadFailedException
	 */
	private void installDirectory(String directoryPath)
			throws LibsLoadFailedException {
		LOG.info("Loading libs from {}", directoryPath);

		File directory = new File(directoryPath);
		File[] fList = directory.listFiles();
		if (fList == null) {
			// invalid directory
			throw new LibsLoadFailedException("Invalid library path: "
					+ directoryPath);
		}
		// load bundles ..
		for (File file : fList) {
			if (file.isFile()) {
				if (file.getName().contains("jar")) {
					LOG.info("Loading lib '{}'", file.getAbsolutePath());
					// load and install as bundle
					String path = "file:///"
							+ file.getAbsolutePath().replace('\\', '/');
					try {
						framework.installBundle(path);
					} catch (BundleInstallFailedException e) {
						LOG.error("Failed to load bundle from {}", path, e);
						throw new LibsLoadFailedException(
								"Failed to load bundle " + path, e);
					}
				}

			}
		}
	}

}
