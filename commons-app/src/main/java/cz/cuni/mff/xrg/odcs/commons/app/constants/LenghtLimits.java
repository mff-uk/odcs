package cz.cuni.mff.xrg.odcs.commons.app.constants;

/**
 * The enum contains length limits based on database schema limitations.
 * @author Petyr
 */
public enum LenghtLimits {
	DPU_NAME(1024),
	DPU_DESCRIPTION(4000),
	DPU_JAR_DESCRIPTION(1024);
	
	/**
	 * Length limit.
	 */
	private final int limit;
	
	private LenghtLimits(int limit) {
		// we decrease the limit by one
		this.limit = limit - 1;
	}
	
	public int limit() {
		return limit;
	}
	
}
