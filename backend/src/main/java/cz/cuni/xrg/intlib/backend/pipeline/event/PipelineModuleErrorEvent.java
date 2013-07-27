package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineModuleErrorEvent extends PipelineEvent {

	private ModuleException exception;
	
    public PipelineModuleErrorEvent(ModuleException exception, DPUInstanceRecord dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.exception = exception;
    }
	
	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR, dpuInstance, execution, 
				"Failed to load DPURecord implementation.", "Loading of DPURecord implementation thrown fallowing exception: " + exception.getMessage());
	}
	
}
