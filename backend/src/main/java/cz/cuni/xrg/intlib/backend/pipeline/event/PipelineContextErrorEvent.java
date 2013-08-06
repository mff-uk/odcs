package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event published in case of error in context processing.
 * 
 * @author Petyr
 * 
 */
public final class PipelineContextErrorEvent extends PipelineExceptionEvent {

	public PipelineContextErrorEvent(ContextException exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(exception, dpuInstance, pipelineExec, source);
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR,
				dpuInstance, execution, "Pipeline execution failed.",
				"Failed to prepare Context for DPURecord because of exception: "
						+ longMessage);
	}

}
