package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 *
 * @author Petyr
 */
public class PipelineStartedEvent extends PipelineEvent {

    public PipelineStartedEvent(PipelineExecution pipelineExec, Object source) {
        super(pipelineExec, source);
    }
}
