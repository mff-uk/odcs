package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;

/**
 * Event published during the pipeline execution termination
 * on user request.
 *
 * @author Petyr
 * 
 */
public class PipelineAbortedEvent extends PipelineEvent {

	/**
	 * The reason for pipeline abort.
	 */
    private final String message;

    public PipelineAbortedEvent(String message, DPUInstanceRecord dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.message = message;
    }

	@Override
	public Record getRecord() {
		return new Record(time, RecordType.PIPELINE_ERROR, dpuInstance, execution, 
				"Pipeline execution aborted.", "Pipeline execution aborted on user request with message: " + message);
	}
}
