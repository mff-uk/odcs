package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
	private Logger logger = LoggerFactory.getLogger(ModuleFacade.class);
	
	/**
	 * Base ctor. The configuration is not used until some other 
	 * method is called. So is not necessary to have all configuration
	 * fully set when passing to the ctor.
	 * @param configuration
	 */
	public ModuleFacade(ModuleFacadeConfig configuration) {
		this.framework = new OSGIFramework();
		this.configuration = configuration;
	}	
	
	/**
	 * Start framework. Must be called as a first method after ctor.
	 * @throws FrameworkStartFailedException
	 * @throws LibsLoadFailedException
	 */
	public void start() throws FrameworkStartFailedException, LibsLoadFailedException {
		logger.info("Starting module facade");
		// start
		framework.start(configuration.getPackagesToExpose());
		// load libs
		try {
			installDirectory(configuration.getDpuLibsFolder());
		} catch (Exception e) {
			logger.error("installDirectory failed", e);
			throw new LibsLoadFailedException(e);
		}
	}
	
	/**
	 * Stop framework. Should be called as last method after 
	 * releasing all used instances from ModuleFacade.
	 */
	public void stop() {
		framework.stop();
	}
	
	/**
	 * Load main class from bundle and return it as object.
	 * @param relativePath Relative path in DPU's directory.
	 * @return Loaded class.
	 * @throws FileNotFoundException 
	 * @throws ClassLoadFailedException 
	 * @throws BundleInstallFailedException 
	 */
	public Object getObject(String relativePath) 
			throws BundleInstallFailedException, ClassLoadFailedException, FileNotFoundException {
		// check existance
		File file = new File(configuration.getDpuFolder() + relativePath);
		if (file.exists()) {
			// ok, file exist ..
		} else {
			throw new FileNotFoundException("File '" + file.getAbsolutePath() + "' does not exist.");
		}
		
		String uri = "file:///" + configuration.getDpuFolder() + relativePath;
		return framework.loadClass(uri);
	}	
	
    /**
     * Return content of manifest for given bundle.
     * @param uri Path to the bundle.
     * @return Description stored in manifest file or null in case of error.
     * @throws MalformedURLException 
     */
    public String getJarDescription(String uri) throws MalformedURLException {
    	// can throw
        URL url = new URL(uri);
    	
        try ( InputStream is = url.openStream()) {        	        	
        	Manifest manifest = new Manifest(is);
        	Attributes mainAttribs = manifest.getMainAttributes();
        	String description = (String)mainAttribs.get("Description");
        	
        	is.close();
        	return description;
        } catch (IOException ex) {
        	logger.error("Failed to read description from {}", uri, ex);
        	// in case of exception return null
        	return null;
        }
    }	
	
	/**
	 * List files in single directory (non-recursive). If the
	 * file is *.jar then load id as a bundle.
	 * @param directoryPath system path to directory. Not prefixed by file:///
	 * @throws LibsLoadFailedException
	 */
	private void installDirectory(String directoryPath) throws LibsLoadFailedException {
		logger.info("Loading libs from {}", directoryPath);
		
		File directory = new File( directoryPath );
		File[] fList = directory.listFiles();
		if (fList == null ){
			// invalid directory
			throw new LibsLoadFailedException("Invalid libs path: " + directoryPath);
		}
		// load bundles .. 
		for (File file : fList){
			if (file.isFile()){
				if (file.getName().contains("jar")) {
					logger.info("Loading lib '{}'", file.getAbsolutePath());
					// load and install as bundle
					String path = "file:///" + file.getAbsolutePath().replace('\\', '/');				
					try {
						framework.installBundle( path );
					} catch (BundleInstallFailedException e) {
						logger.error("Failed to load bundle from {}", path, e);
						throw new LibsLoadFailedException("Failed to load bundle " + path, e);
					}
				}
				
			}
		}
	}
	
}
