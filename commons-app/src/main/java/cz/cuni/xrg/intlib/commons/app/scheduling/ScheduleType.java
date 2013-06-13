package cz.cuni.xrg.intlib.commons.app.scheduling;

/**
 * Type of schedule ie. condition of activation. 
 * 
 * @author Petyr
 *
 */
public enum ScheduleType {
	/**
	 * Activate schedule after another pipeline run finish.
	 */
	AfterPipeline,
	/**
	 * Run in given period.
	 */
	Periodicaly
}
