package cz.cuni.xrg.intlib.backend.context;

/**
 * Exception class used by Context.
 * 
 * @author Petyr
 *
 */
public class ContextException extends Exception {

    public ContextException(Throwable cause) {
        super(cause);
    }

    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextException(String message) {
        super(message);
    }		
	
}
