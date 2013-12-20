package cz.cuni.mff.xrg.odcs.commons.app.module.osgi;

/**
 * Used to announce that {@link OSGIModuleFacade#start} failed.
 * 
 * @author Petyr
 *
 */
public class FrameworkStartFailedException extends Exception {

    public FrameworkStartFailedException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
