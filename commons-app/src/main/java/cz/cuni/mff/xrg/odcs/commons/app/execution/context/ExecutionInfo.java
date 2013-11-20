package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import java.io.File;
import java.util.Set;

/**
 *
 * @author Petyr
 */
public class ExecutionInfo {

	/**
	 * Name of working sub directory.
	 */
	private static final String WORKING_DIR = "working";

	/**
	 * Name of storage directory in which the DataUnits are save into.
	 */
	private static final String STORAGE_DIR = "storage";

	/**
	 * Directory for results.
	 */
	private static final String RESULT_DIR = "result";	
	
	/**
	 * Binded context.
	 */
	private final ExecutionContextInfo executionContext;

	public ExecutionInfo(ExecutionContextInfo executionContext) {
		this.executionContext = executionContext;
	}

	public DpuContextInfo dpu(DPUInstanceRecord dpuInstance) {
		if (executionContext.getContexts().containsKey(dpuInstance)) {
			return new DpuContextInfo(executionContext, dpuInstance, this);
		} else {
			return null;
		}
	}

	public Set<DPUInstanceRecord> getDPUIndexes() {
		return executionContext.getContexts().keySet();
	}
	
	/**
	 * Delete data from context info. The state remain unchanged.
	 */
	public void clear() {
		executionContext.getContexts().clear();
	}
	
	/**
	 * Return relative path from execution directory to the execution root
	 * directory.
	 * 
	 * @return Relative path start but not end with separator separator (/, \\).
	 */
	public String getRootPath() {
		return File.separatorChar + executionContext.getExecution().getId().toString();
	}
	
	/**
	 * Return relative path from execution directory to the execution storage
	 * directory.
	 * 
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getStoragePath() {
		return getRootPath() + File.separatorChar + STORAGE_DIR;
	}
	
	/**
	 * Return relative path from execution directory to the execution result
	 * directory. This directory can be used to store result data.
	 * 
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getResultPath() {
		return getRootPath() + File.separatorChar + RESULT_DIR;
	}
	
	/**
	 * Return relative path from execution directory to the execution working
	 * directory.
	 * 
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getWorkingPath() {
		return getRootPath() + File.separatorChar + WORKING_DIR;
	}
	
}
