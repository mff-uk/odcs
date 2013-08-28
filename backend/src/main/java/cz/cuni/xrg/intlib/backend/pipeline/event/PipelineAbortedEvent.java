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

	public PipelineAbortedEvent(PipelineExecution pipelineExec,	Object source) {
		super(null, pipelineExec, source);
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_INFO,
				dpuInstance, execution, 
				"Pipeline execution aborted.",
				"Pipeline execution aborted on user request.");
	}
}
