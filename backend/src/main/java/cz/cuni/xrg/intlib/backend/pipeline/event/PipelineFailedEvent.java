package cz.cuni.xrg.intlib.backend.pipeline.event;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event is published if the pipeline is terminated due the error in DPURecord.
 * 
 * @author Petyr
 * 
 */
public final class PipelineFailedEvent extends PipelineEvent {

	private String longMessage;

	public PipelineFailedEvent(String message,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(dpuInstance, pipelineExec, source);
		this.longMessage = message;
	}

	public PipelineFailedEvent(Exception exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(dpuInstance, pipelineExec, source);
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);

		this.longMessage = sw.toString();
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR,
				dpuInstance, execution, "Pipeline execution failed.",
				"Pipeline execution terminated because of: " + longMessage);
	}

}
