package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;

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
	 * Return list of DataUnit indexes for DPURecord that can be used in {@link #getTypeForDataUnit} 
	 * and {@link #getDirectoryForDataUnit}
	 * @param dpuInstance
	 * @return Set of indexes or null if there is no data for given dpuInstance.
	 */
	public Set<Integer> getIndexesForDataUnits(DPUInstance dpuInstance);
	
	/**
	 * Return class with information about {@link cz.cuni.xrg.intlib.commons.data.DataUnit} used 
	 * by {@link DPUInstance}
	 * @param dpuInstance {@link DPUInstance} which worked with {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * @param id {@link cz.cuni.xrg.intlib.commons.data.DataUnit}' index.
	 * @return Information about DataUnit or null if the information doesn't exist.
	 */
	public DataUnitInfo getDataUnitInfo(DPUInstance dpuInstance, int index);
		
	/**
	 * Return directory where the result from given DPURecord are be stored.
	 * @param dpuInstance The author of results.
	 * @return Null in case of bad dpuInstance.
	 */
	public File getDirectoryForResult(DPUInstance dpuInstance);
	
	/**
	 * Return path to the file where the log is stored.
	 * @return Path or null if no file with log exist. 
	 */
	public File getLogFile();
}
