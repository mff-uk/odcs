package cz.cuni.xrg.intlib.backend.pipeline.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory
			.getLogger(PipelineRestart.class);
	
    public PipelineRestart(PipelineExecution pipelineExec, Object source) {
        super(null, pipelineExec, source);
        
        LOG.info("Pipeline execution has been restarted");
    }

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_INFO, null, 
				execution, 	"Pipeline execution has been restarted.", "");
	}	
	
}
