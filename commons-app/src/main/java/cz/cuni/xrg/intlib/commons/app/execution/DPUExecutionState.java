package cz.cuni.xrg.intlib.commons.app.execution;

import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;

/**
 * Describe states for DPU execution. 
 * 
 * @see {@link ProcessingUnitInfo}
 * @author Petyr
 *
 */
public enum DPUExecutionState {
	/**
	 * The DPU is in pre-processing state. It's 
	 * the default state.
	 */
	PREPROCESSING,
	/**
	 * The DPU is currently being executed.
	 */
	RUNNING,
	/**
	 * The DPU execution has been finished.
	 */
	FINISHED
}
