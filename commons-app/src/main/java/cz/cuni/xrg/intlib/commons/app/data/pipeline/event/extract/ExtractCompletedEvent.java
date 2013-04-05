package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract;

/**
 * Event is published when an {@link Extractor} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractCompletedEvent extends ExtractEvent {

    public ExtractCompletedEvent(Extractor extractor, ExtractContext context, Object source) {
        super(extractor, context, source);
    }
}