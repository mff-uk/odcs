package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Base module exception.
 * 
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
}
