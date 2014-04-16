package cz.cuni.mff.xrg.odcs.commons.app.module;

/**
 * Report failure during validation of DPU.
 * 
 * @author Petyr
 *
 */
public class DPUValidatorException extends Exception {

	/**
	 * 
	 * @param cause Cause of the {@link DPUCreateException}.
	 */			
	public DPUValidatorException(String cause) {
		super(cause);
	}
	
}
