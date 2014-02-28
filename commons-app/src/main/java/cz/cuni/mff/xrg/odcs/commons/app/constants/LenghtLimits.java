package cz.cuni.mff.xrg.odcs.commons.app.constants;

/**
 * The enum contains length limits based on database schema limitations.
 * 
 * @author Petyr
 */
public enum LenghtLimits {
	DPU_NAME(1024),
	DPU_TOOL_TIP(512),
	DPU_JAR_DESCRIPTION(1024),
	DATAUNIT_NAME(2048),
	SHORT_MESSAGE(128),
	SCHEDULE_NAME(1024),
	PIPELINE_NAME(1024),
	LOGGER_NAME(254);

	/**
	 * Length limit.
	 */
	private final int limit;
	
	private LenghtLimits(int limit) {
		// we decrease the limit by one
		this.limit = limit - 1;
	}
	
	/**
	 * @return the maximum length or -1 if the length is limitless.
	 */
	public int limit() {
		return limit;
	}
	
}
