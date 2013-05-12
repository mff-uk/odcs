package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineModuleErrorEvent extends PipelineEvent {

	private ModuleException exception;
	
    public PipelineModuleErrorEvent(ModuleException exception, PipelineExecution pipelineExec, Object source) {
        super(pipelineExec, source);
        this.exception = exception;
    }
	
    public ModuleException getModuleException() {
    	return exception;
    }	
	
}
