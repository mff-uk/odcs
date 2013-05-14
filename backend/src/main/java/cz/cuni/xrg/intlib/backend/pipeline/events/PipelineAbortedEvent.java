package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

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

    public PipelineAbortedEvent(String message, DPUInstance dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.message = message;
    }

	@Override
	public Record getRecord() {
		return new Record(time, RecordType.PIPELINEERROR, dpuInstance, execution, 
				"Pipeline execution aborted.", "Pipeline execution aborted on user request with message: " + message);
	}
}
