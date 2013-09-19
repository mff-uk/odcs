package cz.cuni.xrg.intlib.commons.app.module;

import java.util.Dictionary;
import java.util.List;

import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;

/**
 * Provide interface for manipulation with DPU's instances. 
 * 
 * @author Petyr
 *
 */
public interface ModuleFacade {

	/**
	 * Return instance for given {@link DPUTemplateRecord}.
	 * @param dpu
	 * @return
	 * @throws ModuleException
	 */
	Object getInstance(DPUTemplateRecord dpu) throws ModuleException;
		
	/**
	 * Unload the given {@link DPUTemplateRecord} instance bundle.
	 * @param dpu
	 */
	void unLoad(DPUTemplateRecord dpu);
	
	/**
	 * Start update on given DPU. Such DPU will block every attempt 
	 * to load class from it until {@link #endUpdate(String)} is called.
	 * 
	 * If return false there is no point to continue by calling update. Instead
	 * the {@link #getInstance(DPUTemplateRecord)}.
	 * 
	 * @param directory Directory which determine DPU.
	 * @return False if such bundle is not loaded.
	 */
	boolean beginUpdate(String directory);	
	
	/**
	 * Update bundle. The bundle is determined by it's directory. If
	 * such bundle is not loaded into application than nothing happened.
	 * 
	 * Is the only nonblocking function can be called between 
	 * {@link #beginUpdate(String)} and {@link #endUpdate(String)}.
	 * 
	 * After the bundle is updated then try to load main class from it. The
	 * load process can throw exception like {@link #getInstance(DPUTemplateRecord)}
	 * 
	 * @param directory Bundle's directory.
	 * @param newName Name of jar-file that should be reloaded.
	 * @throws ModuleException
	 */
	void update(String directory, String newName) throws ModuleException;
		
	/**
	 * Stop update on given DPU.
	 * @param directory Directory which determine DPU.
	 */
	void endUpdate(String directory);
	
	/**
	 * Return jar-properties for given {@link DPURecord}'s bundle 
	 * @param relativePath
	 * @return
	 */
	public Dictionary<String, String> getJarProperties(DPUTemplateRecord dpu);
	
	/**
	 * Install all jar files from given folders as libraries.
	 * @param directoryPaths
	 */
	void loadLibs(List<String> directoryPaths);
	
}
