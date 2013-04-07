package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract;

import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.ETLEvent;
import cz.cuni.xrg.intlib.commons.app.data.Extractor;
/**
 * Base class for {@link Extractor} events
 *
 * @see Extractor
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class ExtractEvent extends ETLEvent {

    protected final Extractor extractor;
    protected final ExtractContext extractContext;

    public ExtractEvent(Extractor extractor, ExtractContext context, Object source) {
        super(source);
        this.extractor = extractor;
        this.extractContext = context;
    }

    /**
     * Returns the {@link Extractor} associated with this event.
     *
     * @return
     */
    public Extractor getExtractor() {
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
