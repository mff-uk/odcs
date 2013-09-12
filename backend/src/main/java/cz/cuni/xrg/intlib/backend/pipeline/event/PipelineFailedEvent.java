package cz.cuni.xrg.intlib.backend.pipeline.event;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.execution.dpu.StructureException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event is published if the pipeline is terminated due the error. To create the
 * event use static methods.
 * 
 * @author Petyr
 * 
 */
public final class PipelineFailedEvent extends PipelineEvent {

	private String shortMessage;

	private String longMessage;

	protected PipelineFailedEvent(String shortMessage,
			String longMessage,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(dpuInstance, pipelineExec, source);
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	protected PipelineFailedEvent(String shortMessage,
			Throwable exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(dpuInstance, pipelineExec, source);
		this.shortMessage = shortMessage;
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		this.longMessage = sw.toString();
	}

	protected PipelineFailedEvent(String shortMessage,
			String longMessagePrefix,
			Throwable exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(dpuInstance, pipelineExec, source);
		this.shortMessage = shortMessage;
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		this.longMessage = longMessagePrefix + sw.toString();
	}

	public static PipelineFailedEvent create(String shortMessage,
			String longMessage,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		return new PipelineFailedEvent(shortMessage, longMessage, dpuInstance,
				pipelineExec, source);
	}

	public static PipelineFailedEvent create(Exception exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		return new PipelineFailedEvent("Pipeline execution failed.",
				"Execution failed because: " + exception.getMessage(),
				exception, dpuInstance, pipelineExec, source);
	}

	public static PipelineFailedEvent create(Error error,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		return new PipelineFailedEvent("Pipeline execution failed.",
				"Execution failed due to error: " + error.getMessage(), error,
				dpuInstance, pipelineExec, source);
	}

	public static PipelineFailedEvent create(ContextException exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		return new PipelineFailedEvent(
				"Pipeline execution failed.",
				"Failed to prepare Context for DPURecord because of exception: ",
				exception, dpuInstance, pipelineExec, source);
	}

	public static PipelineFailedEvent create(ModuleException exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		return new PipelineFailedEvent(
				"Pipeline execution failed.",
				"Loading of DPURecord implementation thrown fallowing exception: ",
				exception, dpuInstance, pipelineExec, source);
	}

	public static PipelineFailedEvent create(StructureException exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		return new PipelineFailedEvent("Pipeline execution failed.",
				"Root structure exception: ", exception, dpuInstance,
				pipelineExec, source);
	}

	/**
	 * Create event which indicate that there is no jar-file for DPU to execute.
	 * 
	 * @param context
	 * @param source
	 * @return
	 */
	public static PipelineFailedEvent createMissingFile(DPUInstanceRecord dpu,
			PipelineExecution execution,
			Object source) {
		StringBuilder longMessage = new StringBuilder();
		longMessage.append("Missing jar-file for DPU: '");
		longMessage.append(dpu.getName());
		longMessage.append("'");
		return new PipelineFailedEvent("Missing DPU.",
				longMessage.toString(), dpu, execution, source);
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR,
				dpuInstance, execution, shortMessage, longMessage);
	}
}
