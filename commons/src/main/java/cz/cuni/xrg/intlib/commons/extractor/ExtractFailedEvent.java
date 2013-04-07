package cz.cuni.xrg.intlib.commons.extractor;

/**
 * Event is published when an {@link Extract} could not complete because an error occurred.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractFailedEvent extends ExtractEvent {

    private final ExtractException exception;

    public ExtractFailedEvent(ExtractException exception, Extract extractor, ExtractContext context, Object source) {
        super(extractor, context, source);
        this.exception = exception;
    }

    public ExtractException getException() {
        return exception;
    }
}