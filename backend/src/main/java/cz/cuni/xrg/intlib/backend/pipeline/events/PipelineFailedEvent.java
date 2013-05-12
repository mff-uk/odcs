package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineFailedEvent extends PipelineEvent {

    private final String message;

    public PipelineFailedEvent(String message, PipelineExecution pipelineExec, Object source) {
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
