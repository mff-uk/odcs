package cz.cuni.mff.xrg.odcs.backend.pipeline.event;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Event that is used to publish informations about pipeline execution. 
 * 
 * @author Petyr
 *
 */
public class PipelineInfo extends PipelineEvent {

	private String shortMessage;
	
	private String longMessage;
	
	protected PipelineInfo(PipelineExecution execution,
			Object source,
			String shortMessage,
			String longMessage) {
		super(null, execution, source);
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_INFO, null, execution, shortMessage, longMessage);
	}

	public static PipelineInfo createWait(PipelineExecution execution,
			Object source) {
		return new PipelineInfo(execution, source, "Pipeline is waiting as there are conflicts pipeline running", "");
	}
	
	public static PipelineInfo createWaitEnd(PipelineExecution execution,
			Object source) {
		return new PipelineInfo(execution, source, "Pipeline continue", "");
	}	
	
}
