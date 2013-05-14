package cz.cuni.xrg.intlib.backend.pipeline.events;

import cz.cuni.xrg.intlib.backend.execution.StructureException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

public class PipelineStructureError extends PipelineEvent {

	private StructureException exception;
	
    public PipelineStructureError(StructureException exception, DPUInstance dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.exception = exception;
    }	
	
	@Override
	public Record getRecord() {
		return new Record(time, RecordType.PIPELINEERROR, dpuInstance, execution, 
				"Pipeline structure error.", "Exception: " + exception.getMessage());
	}   
}
