package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event indicating that a {@link ETLPipelineImpl} has completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 * @author Petyr
 */
public class PipelineCompletedEvent extends PipelineEvent {

    protected final long duration;

    public PipelineCompletedEvent(long duration, PipelineExecution pipelineExec, Object source) {
        super(pipelineExec, source);
        this.duration = duration;
    }

    /**
     * Returns the total duration (in ms) the pipeline took to execute.
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }
}