package cz.cuni.xrg.intlib.commons.loader;

import cz.cuni.xrg.intlib.commons.DPUException;

/**
 * Exception thrown by a loader if something goes wrong throughout the
 * loading process.
 *
 * @see Load
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadException extends DPUException {

    public LoadException(Throwable cause) {
        super(cause);
    }

    public LoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadException(String message) {
        super(message);
    }
}
