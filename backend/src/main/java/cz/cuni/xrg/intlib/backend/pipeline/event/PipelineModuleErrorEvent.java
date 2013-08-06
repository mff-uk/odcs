package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Report execution termination because of exception in DPU.
 * 
 * @author Petyr
 *
 */
public final class PipelineModuleErrorEvent extends PipelineExceptionEvent {

	public PipelineModuleErrorEvent(ModuleException exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(exception, dpuInstance, pipelineExec, source);
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR,
				dpuInstance, execution,
				"Failed to load DPURecord implementation.",
				"Loading of DPURecord implementation thrown fallowing exception: "
						+ longMessage);
	}

}
