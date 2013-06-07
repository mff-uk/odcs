package cz.cuni.xrg.intlib.commons.app.scheduling;

/**
 * Types of plan.
 * 
 * @author Petyr
 *
 */
public enum PlanType {
	/**
	 * Execute pipeline in given time.
	 */
	RunInTime,
	/**
	 * Execute pipeline after another pipeline ends.
	 */
	RunAfterPipeline
}
