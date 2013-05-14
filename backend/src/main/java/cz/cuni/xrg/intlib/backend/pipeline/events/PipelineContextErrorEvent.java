package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineContextErrorEvent extends PipelineEvent {

	private ContextException exception;
	
    public PipelineContextErrorEvent(ContextException exception, DPUInstance dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.exception = exception;
    }

    @Override
	public Record getRecord() {
    	return new Record(time, RecordType.PIPELINEERROR, dpuInstance, execution, 
    			"Pipeline execution failed.", "Failed to prepare Context for DPU because of exception: " + exception.getMessage());
	}
    
}
