package cz.cuni.xrg.intlib.frontend.browser;

/**
 * Indicates that the initialisation of DataUnitBrowser failed.
 * 
 * @author Petyr
 *
 */
public class BrowserInitFailedException extends Exception {

    public BrowserInitFailedException(Throwable cause) {
        super(cause);
    }

    public BrowserInitFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrowserInitFailedException(String message) {
        super(message);
    }		
	
}
