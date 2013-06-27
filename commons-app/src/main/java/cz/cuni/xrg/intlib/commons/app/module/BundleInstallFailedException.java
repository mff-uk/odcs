package cz.cuni.xrg.intlib.commons.app.module;

/**
 * 
 * 
 * @author Petyr
 *
 */
public class BundleInstallFailedException extends ModuleException {

	public BundleInstallFailedException(Throwable cause) {
        super(cause);
    }

    public BundleInstallFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BundleInstallFailedException(String message) {
        super(message);
    }		
	
}
