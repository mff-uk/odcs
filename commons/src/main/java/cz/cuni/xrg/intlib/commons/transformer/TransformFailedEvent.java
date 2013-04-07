package cz.cuni.xrg.intlib.commons.transformer;

/**
 * Published when a {@link Transform} throws an exception during transformation.
 *
 * @see Transform
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformFailedEvent extends TransformEvent {

    private Exception exception;

    public TransformFailedEvent(Exception exception, Transform transformer, TransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}