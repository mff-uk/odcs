package cz.cuni.xrg.intlib.commons.app.module;

/**
 * 
 * 
 * @author Petyr
 *
 */
public class ClassLoadFailedException extends ModuleException {

	public ClassLoadFailedException(Throwable cause) {
        super(cause);
    }

    public ClassLoadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassLoadFailedException(String message) {
        super(message);
    }		
	
}
