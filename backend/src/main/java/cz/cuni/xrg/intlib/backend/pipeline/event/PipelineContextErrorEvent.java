package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;

public class PipelineContextErrorEvent extends PipelineEvent {

	private ContextException exception;
	
    public PipelineContextErrorEvent(ContextException exception, DPUInstanceRecord dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.exception = exception;
    }

    @Override
	public Record getRecord() {
    	return new Record(time, RecordType.PIPELINE_ERROR, dpuInstance, execution, 
    			"Pipeline execution failed.", "Failed to prepare Context for DPURecord because of exception: " + exception.getMessage());
	}
    
}
