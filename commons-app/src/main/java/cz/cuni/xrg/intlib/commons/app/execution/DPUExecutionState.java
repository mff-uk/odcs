package cz.cuni.xrg.intlib.commons.app.execution;

/**
 * Describe states for DPU execution. 
 * 
 * @see {@link ProcessingUnitInfo}
 * @author Petyr
 *
 */
public enum DPUExecutionState {
	/**
	 * The DPU is currently being executed.
	 */
	RUNNING,
	/**
	 * The DPU execution has been finished.
	 */
	FINISHED
}
