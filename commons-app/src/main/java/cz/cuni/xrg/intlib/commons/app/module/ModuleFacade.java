package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.app.module.osgi.*;

/**
 * Facade providing actions with DPU module implementations.
 * 
 * @author Petyr
 *
 */
public class ModuleFacade {
	
	/**
	 * Used framework.
	 */
	private Framework framework;
	
	/**
	 * Configuration.
	 */
	private ModuleFacadeConfiguration configuration;
		
	/**
	 * Base ctor. The configuration is not used until some other 
	 * method is called. So is not necessary to have all configuration
	 * fully set when passing to the ctor.
	 * @param configuration
	 */
	public ModuleFacade(ModuleFacadeConfiguration configuration) {
		this.framework = new Framework();
		this.configuration = configuration;
	}	
	
	/**
	 * Start framework. Must be called as a first method after ctor.
	 */
	public void start() throws ModuleException {
		// start
		this.framework.start(configuration.getPackagesToExpose());
	}
	
	/**
	 * Stop framework. Should be called as last method after 
	 * releasing all used instances from ModuleFacade.
	 */
	public void stop() {
		this.framework.uninstallBundles();
		this.framework.stop();		
	}
	
	/**
	 * Try to load DPUExecutive from given path.
	 * @param path path to bundle (jar file) relative to dpu's directory
	 * @return loaded module
	 * @throws ModuleException
	 */
	public DPUExecutive getInstance(String path) throws ModuleException {
		return this.framework.loadDPU(configuration.getDpuFolder() + path);
	}
	
	/**
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded loader
	 * @throws ModuleException
	 */
	public Load getInstanceLoader(String uri) throws ModuleException {
		return (Load) getInstance(uri);
	}
	
	/**
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded extractor
	 * @throws ModuleException
	 */
	public Extract getInstanceExtract(String uri) throws ModuleException {
		return (Extract) getInstance(uri);
	}
	
	/**
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded transformer
	 * @throws ModuleException
	 */
	public Transform getInstanceTransform(String uri) throws ModuleException {
		return (Transform) getInstance(uri);
	}
	
	/**
	 * List files in single directory (non-recursive). If the
	 * file is *.jar then load id as a bundle.
	 * @param directoryPath system path to directory. Not url.
	 */
	public void installDirectory(String directoryPath) {
		String message = "";
// TODO: Propagate exceptions to application ..		
		File directory = new File( directoryPath );
		File[] fList = directory.listFiles();
		for (File file : fList){
			if (file.isFile()){
				if (file.getName().contains("jar")) {
					// load as bundle
					// install bundle
					String path = "file:///" + file.getAbsolutePath().replace('\\', '/');
					message += "loading: " + path + "\n";
					try {
						framework.installBundle( path );
					} catch (OSGiException e) {
						message += e.getMessage() + " > " + e.getOriginal().getMessage() + "\n";
					} catch(Exception e) {
						message += "Exception: " + e.getMessage() + "\n";
					}							
				}
				
			}
		}
	}
		
}
