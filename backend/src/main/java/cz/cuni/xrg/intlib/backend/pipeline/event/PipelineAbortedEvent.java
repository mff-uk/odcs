package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event published during the pipeline execution termination on user request.
 * 
 * @author Petyr
 * 
 */
public final class PipelineAbortedEvent extends PipelineEvent {

	/**
	 * The reason for pipeline abort.
	 */
	private final String message;

	public PipelineAbortedEvent(String message,
			PipelineExecution pipelineExec,
			Object source) {
		super(pipelineExec, source);
		this.message = message;
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_ERROR,
				dpuInstance, execution, "Pipeline execution aborted.",
				"Pipeline execution aborted on user request with message: "
						+ message);
	}
}
