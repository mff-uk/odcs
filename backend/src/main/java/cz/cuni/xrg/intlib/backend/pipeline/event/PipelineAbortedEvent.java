package cz.cuni.xrg.intlib.backend.pipeline.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(PipelineAbortedEvent.class);
	
	public PipelineAbortedEvent(PipelineExecution pipelineExec,	Object source) {
		super(null, pipelineExec, source);
		
		LOG.info("Pipeline aborted on user request.");
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_INFO,
				dpuInstance, execution, 
				"Pipeline execution aborted.",
				"Pipeline execution aborted on user request.");
	}
}
