package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;

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
	public boolean containsData(DPUInstanceRecord dpuInstance);
	
	/**
	 * Return list of DataUnit indexes for DPUInstanceRecord that can be used in {@link #getInputInfo}.
	 * @param dpuInstance Instance of DPU for which DataUnit retrieve DataUnit's indexes.
	 * @return Set of indexes or null if there is no data for given dpuInstance.
	 */
	public Set<Integer> getIndexesForDataUnits(DPUInstanceRecord dpuInstance);
	
	/**
	 * Return class with information about {@link cz.cuni.xrg.intlib.commons.data.DataUnit} used 
	 * by {@link DPUInstanceRecord}
	 * @param dpuInstance DPUInstanceRecord which worked with DataUnit.
	 * @param id DataUnit's index.
	 * @return Information about DataUnit or null if the information doesn't exist.
	 */
	public DataUnitInfo getDataUnitInfo(DPUInstanceRecord dpuInstance, int index);
	
	/**
	 * Return directory where the result from given DPURecord are be stored.
	 * @param dpuInstance The author of results.
	 * @return Null in case of bad dpuInstance.
	 */
	public File getDirectoryForResult(DPUInstanceRecord dpuInstance);
}
