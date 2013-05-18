package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Read-only interface for context. Use this to examine
 * context after pipeline execution has been finished. 
 * 
 * @author Petyr
 *
 */
public interface ExecutionContextReader {
	
	/**
	 * Return true if the context has some data about
	 * execution of certain {@link DPUInstance}
	 * @param dpuInstance
	 * @return 
	 */
	public boolean containsData(DPUInstance dpuInstance);
	
	/**
	 * Return type of {@link cz.cuni.xrg.intlib.commons.data.DataUnit} used 
	 * by {@link DPUInstance}
	 * @param dpuInstance {@link DPUInstance} which worked with {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * @param id {@link cz.cuni.xrg.intlib.commons.data.DataUnit}' index.
	 * @return Type of {@link cz.cuni.xrg.intlib.commons.data.DataUnit} on null if context doesn't exist.
	 */
	public DataUnitType getTypeForDataUnit(DPUInstance dpuInstance, int index);
	
	/**
	 * Return access to the directory where the DataUnit content should be stored.
	 * @param dpuInstance {@link DPUInstance} which worked with {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * @param index {@link cz.cuni.xrg.intlib.commons.data.DataUnit}' index.
	 * @return Directory or null in case that the content does't exist.
	 */	
	public File getDirectoryForDataUnit(DPUInstance dpuInstance, int index);
	
	/**
	 * Return directory where the result from given DPU are be stored.
	 * @param dpuInstance The author of results.
	 * @return Null in case of bad dpuInstance.
	 */
	public File getDirectoryForResult(DPUInstance dpuInstance);
}
