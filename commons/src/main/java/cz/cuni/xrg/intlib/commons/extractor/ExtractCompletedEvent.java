package cz.cuni.xrg.intlib.commons.extractor;

/**
 * Event is published when an {@link Extract} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractCompletedEvent extends ExtractEvent {

    public ExtractCompletedEvent(Extract extractor, ExtractContext context, Object source) {
        super(extractor, context, source);
    }
}