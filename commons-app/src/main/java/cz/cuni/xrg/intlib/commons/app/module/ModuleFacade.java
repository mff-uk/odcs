package cz.cuni.xrg.intlib.commons.app.module;

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
	 * Base ctor.
	 */
	public ModuleFacade() {
		this.framework = new Framework();
	}
	
	/**
	 * Start framework. Must be called as a first method after ctor.
	 */
	public void start() {
		this.framework.start();
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
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded module
	 * @throws ModuleException
	 */
	public DPUExecutive getInstance(String uri) throws ModuleException {
		return this.framework.loadDPU(uri);
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
}
