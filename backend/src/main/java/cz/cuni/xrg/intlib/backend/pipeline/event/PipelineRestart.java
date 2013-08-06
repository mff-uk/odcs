package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Report pipeline restart. Use if backend crash or has been shutdown.
 * 
 * @author Petyr
 *
 */
public final class PipelineRestart extends PipelineEvent {

    public PipelineRestart(PipelineExecution pipelineExec, Object source) {
        super(pipelineExec, source);
    }

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_INFO, null, execution, 
				"Pipeline execution restart.", "");
	}	
	
}
