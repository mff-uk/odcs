package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.backend.execution.StructureException;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineStructureError extends PipelineEvent {

	private StructureException exception;
	
    public PipelineStructureError(StructureException exception, PipelineExecution pipelineExec, Object source) {
        super(pipelineExec, source);
        this.exception = exception;
    }	
	
    public StructureException getMessage() {
        return exception;
    }    
}
