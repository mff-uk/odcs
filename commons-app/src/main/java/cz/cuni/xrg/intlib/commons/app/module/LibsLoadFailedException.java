package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Class reporting failure during libs loading.
 * 
 * @author Petyr
 *
 */
public class LibsLoadFailedException extends ModuleException {

	public LibsLoadFailedException(Throwable cause) {
        super(cause);
    }

    public LibsLoadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibsLoadFailedException(String message) {
        super(message);
    }		
	
}
