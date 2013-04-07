package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.event.ETLEvent;

/**
 * Base class for {@link Extract} events
 *
 * @see Extract
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class ExtractEvent extends ETLEvent {

    protected final Extract extractor;
    protected final ExtractContext extractContext;

    public ExtractEvent(Extract extractor, ExtractContext context, Object source) {
        super(source);
        this.extractor = extractor;
        this.extractContext = context;
    }

    /**
     * Returns the {@link Extract} associated with this event.
     *
     * @return
     */
    public Extract getExtractor() {
        return extractor;
    }

    /**
     * Returns the {@link ExtractContext} of this execution.
     *
     * @return
     */
    public ExtractContext getExtractContext() {
        return extractContext;
    }
}
