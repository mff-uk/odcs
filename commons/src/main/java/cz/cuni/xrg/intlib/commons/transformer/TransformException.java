package cz.cuni.xrg.intlib.commons.transformer;

/**
 * Exception thrown by a transformer if something goes wrong throughout the
 * transformation process.
 *
 * @see Transform
 * @author Petyr
 */
public class TransformException extends Exception {

    public TransformException(Throwable cause) {
        super(cause);
    }

    public TransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformException(String message) {
        super(message);
    }
}
