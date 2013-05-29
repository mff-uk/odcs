package cz.cuni.xrg.intlib.backend.extractor.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.commons.extractor.Extract;

/**
 * Base class for {@link Extract} events
 *
 * @see Extract
 */
public abstract class ExtractEvent extends DPUEvent {

    protected final Extract extractor;
    
    protected final ExtendedExtractContext extractContext;

    public ExtractEvent(Extract extractor, ExtendedExtractContext context, Object source) {
        super(source, context.getDPUInstance(), context.getPipelineExecution());
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
     * Returns the {@link ExtendedExtractContext} of this execution.
     *
     * @return
     */
    public ExtendedExtractContext getExtractContext() {
        return extractContext;
    }
}
