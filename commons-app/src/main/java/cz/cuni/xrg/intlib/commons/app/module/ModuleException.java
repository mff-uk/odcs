package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Module exception.
 * @author Petyr
 *
 */
public class ModuleException extends RuntimeException {

	/**
	 * Store original exception.
	 */
	protected Exception originalException;
	
	/**
	 * @param message Exception message.
	 * @param ex Original exception.
	 */
	public ModuleException(String message, Exception ex) {
		super(message);
		this.originalException = ex;
	}
	
	/**
	 * Return original exception.
	 * @return original exception
	 */
	public Exception getOriginal() {
		return this.originalException;
	}
	
	/**
	 * Return exception message extended by original exception message.
	 * @return
	 */
	public String getTraceMessage() {
		if (originalException == null) {
			return "Exception: " + getMessage();
		} else {
			return "Exception: " + getMessage() + " caused by: " + originalException.getMessage();
		}
	}
	
}
