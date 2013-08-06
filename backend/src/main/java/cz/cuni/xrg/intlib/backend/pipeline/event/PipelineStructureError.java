package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.backend.execution.StructureException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event is used to report structure exception that occure during pipeline
 * execution.
 * 
 * @author Petyr
 * 
 */
public final class PipelineStructureError extends PipelineExceptionEvent {

	public PipelineStructureError(StructureException exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(exception, dpuInstance, pipelineExec, source);
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR,
				dpuInstance, execution, "Pipeline structure error.",
				"Exception: " + longMessage);
	}
}
