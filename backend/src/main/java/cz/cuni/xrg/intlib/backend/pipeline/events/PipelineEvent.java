package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.event.DPUEvent;

/**
 * Base class for {@link ETLPipelineImpl} events
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class PipelineEvent extends DPUEvent {

    protected final Pipeline pipeline;
    protected final String id;

    public PipelineEvent(Pipeline pipeline, String runId, Object source) {
        super(source);
        this.pipeline = pipeline;
        this.id = runId;
    }

    /**
     * Returns the pipeline, where event is registred.
     *
     * @return
     */
    public Pipeline getPipeline() {
        return pipeline;
    }

    /**
     * Returns the unique identifier of this event (!= pipeline id)
     *
     * @return
     */
    public String getId() {
        return id;
    }
}