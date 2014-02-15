package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.Dictionary;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;

/**
 * Provide interface for manipulation with DPU's instances. 
 * 
 * @author Petyr
 *
 */
public interface ModuleFacade {
	
	/**
	 * Return instance for given {@link DPUTemplateRecord}.
	 * 
	 * @param dpu
	 * @return DPU instance for given DPU template
	 * @throws ModuleException
	 */
	Object getInstance(DPUTemplateRecord dpu) throws ModuleException;
	
	/**
	 * Unload the given {@link DPUTemplateRecord} instance bundle.
	 * 
	 * @param dpu
	 */
	void unLoad(DPUTemplateRecord dpu);
	
	/**
	 * Unload the instance bundle given by its directory.
	 * 
	 * @param directory
	 */
	void unLoad(String directory);
	
	/**
	 * Start update on given DPU. This will block access the given
	 * {@link DPUTemplateRecord} from any other thread.
	 * 
	 * Should be called on valid instance.
	 * 
	 * @param dpu
	 */
	void beginUpdate(DPUTemplateRecord dpu);	
	
	/**
	 * Update bundle. The bundle is determined by it's directory. If
	 * such bundle is not loaded into application than nothing happened.
	 * 
	 * After the bundle is updated then try to load main class from it. The
	 * load process can throw exception like {@link #getInstance(DPUTemplateRecord)}
	 * 
	 * In case of error (exception) the bundle is automatically uninstalled. So
	 * there is no reason to call {@link #unLoad(DPUTemplateRecord)}.
	 * 
	 * @param directory Bundle's directory.
	 * @param newName Name of jar-file that should be reloaded.
	 * @throws ModuleException
	 * @return New DPU's main class.
	 */
	Object update(String directory, String newName) throws ModuleException;
		
	/**
	 * Stop update on given DPU. If updataFailed is true, then 
	 * possibly loaded bundle for given DPU is uninstalled.
	 * 
	 * @param dpu
	 * @param updataFailed
	 */
	void endUpdate(DPUTemplateRecord dpu, boolean updataFailed);
	
	/**
	 * Uninstall and delete the DPU's jar file.
	 * 
	 * @param dpu
	 */
	void delete(DPUTemplateRecord dpu);
	
	/**
	 * Return jar-properties for given DPU template's bundle.
	 * 
	 * @param dpu
	 * @return jar-properties for given {@link DPUTemplateRecord}'s bundle.
	 */
	Dictionary<String, String> getJarProperties(DPUTemplateRecord dpu);
	
	/**
	 * Pre-load bundles for all DPUs persisted in database into memory. Do not
	 * create instance from them, so their functionality is not validated.
	 */
	void preLoadAllDPUs();
	
	/**
	 * Pre-load bundles for given DPUs into memory. Do not create instance
	 * from them, so their functionality is not validated.
	 * 
	 * @param dpus
	 */
	void preLoadDPUs(List<DPUTemplateRecord> dpus);
	
	/**
	 * Install all jar files from given folders as libraries.
	 * 
	 * @param directoryPaths
	 */
	void loadLibs(List<String> directoryPaths);
	
	/**
	 * @return path to the DPU directory.
	 */
	String getDPUDirectory();
	
}
