package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.DPUException;

/**
 * Exception thrown by an extractor if something goes wrong throughout the
 * extraction process.
 *
 * @see Extract
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractException extends DPUException {

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
