package cz.cuni.mff.xrg.odcs.commons.app.module;

/**
 * Exception indicates failure during DPU's creation process.
 * 
 * @author Petyr
 *
 */
public class DPUCreateException extends Exception {

	public DPUCreateException(String message) {
		super(message);
	}

	public DPUCreateException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
