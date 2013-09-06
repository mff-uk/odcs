package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Exception thrown when OSGI bundle cannot be installed, because ... TODO Petyr
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
