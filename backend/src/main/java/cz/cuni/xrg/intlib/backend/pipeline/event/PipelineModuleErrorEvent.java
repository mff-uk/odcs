package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;

public class PipelineModuleErrorEvent extends PipelineEvent {

	private ModuleException exception;
	
    public PipelineModuleErrorEvent(ModuleException exception, DPUInstanceRecord dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.exception = exception;
    }
	
	@Override
	public Record getRecord() {
		return new Record(time, RecordType.PIPELINE_ERROR, dpuInstance, execution, 
				"Failed to load DPURecord implementation.", "Loading of DPURecord implementation thrown fallowing exception: " + exception.getMessage());
	}
	
}
