package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Published when a component {@link Extract}, {@link Transform},
 * {@link Load} requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline(java.lang.String)}.<br/>
 * The pipeline exits directly after this event is published.
 *
 * @see ProcessingContext#cancelPipeline(java.lang.String)
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class PipelineAbortedEvent extends PipelineEvent {

    private final String message;

    public PipelineAbortedEvent(String message, PipelineExecution pipelineExec, String id, Object source) {
        super(pipelineExec, source);
        this.message = message;
    }

    /**
     * Returns the cancellation message
     *
     * @return
     */
    public String getMessage() {
        return message;
    }
}
