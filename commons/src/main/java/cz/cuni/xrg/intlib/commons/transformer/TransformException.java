package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.DPUException;

/**
 * Exception thrown by a transformer if something goes wrong throughout the
 * transformation process.
 *
 * @see Transform
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformException extends DPUException {

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
