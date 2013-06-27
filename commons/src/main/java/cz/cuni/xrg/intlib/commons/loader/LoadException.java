package cz.cuni.xrg.intlib.commons.loader;

/**
 * Exception thrown by a loader if something goes wrong throughout the
 * loading process.
 *
 * @see Load
 * @author Petyr
 */
public class LoadException extends Exception {

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
