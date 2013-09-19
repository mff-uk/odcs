package cz.cuni.xrg.intlib.commons.app.module.osgi;

/**
 * Used to announce that {@link OSGIModuleFacade#start} failed.
 * @author Petyr
 *
 */
public class FrameworkStartFailedException extends Exception {

    public FrameworkStartFailedException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
