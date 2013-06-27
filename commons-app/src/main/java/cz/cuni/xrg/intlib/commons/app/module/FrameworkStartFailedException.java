package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Exception for report failures during OSGI framework start up.
 * 
 * @author Petyr
 *
 */
public class FrameworkStartFailedException extends ModuleException {

	public FrameworkStartFailedException(Throwable cause) {
        super(cause);
    }

    public FrameworkStartFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkStartFailedException(String message) {
        super(message);
    }		
	
}
