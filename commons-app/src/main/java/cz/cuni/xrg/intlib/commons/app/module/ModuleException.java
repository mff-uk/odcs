package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Module exception.
 * @author Petyr
 *
 */
public class ModuleException extends RuntimeException {

	public ModuleException(Throwable cause) {
        super(cause);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleException(String message) {
        super(message);
    }
	
	/**
	 * Return original exception.
	 * @return original exception
	 */
	public Exception getOriginal() {
		return null;
	}
	
	/**
	 * Return exception message extended by original exception message.
	 * @return
	 */
	public String getTraceMessage() {
		return getMessage();
	}
	
}
