package cz.cuni.xrg.intlib.commons.app.module.osgi;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;

/**
 * OSGi framework exception.
 *
 * @author Petyr
 *
 */
public class OSGiException extends ModuleException {

    public OSGiException(Throwable cause) {
        super(cause);
    }

    public OSGiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OSGiException(String message) {
        super(message);
    }
    
}
