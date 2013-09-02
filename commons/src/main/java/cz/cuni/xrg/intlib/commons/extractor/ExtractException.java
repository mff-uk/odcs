package cz.cuni.xrg.intlib.commons.extractor;

/**
 * Exception thrown by an extractor if something goes wrong throughout the
 * extraction process.
 *
 * @see Extract
 * @author Petyr
 */
@Deprecated
public class ExtractException extends Exception {

    public ExtractException(Throwable cause) {
        super(cause);
    }

    public ExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractException(String message) {
        super(message);
    }
}
