package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineContextErrorEvent extends PipelineEvent {

	private ContextException exception;
	
    public PipelineContextErrorEvent(ContextException exception, PipelineExecution pipelineExec, Object source) {
        super(pipelineExec, source);
        this.exception = exception;
    }
	
    public ContextException getContextException() {
    	return exception;
    }
    
}
