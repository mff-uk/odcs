package cz.cuni.xrg.intlib.backend.pipeline.event;

import cz.cuni.xrg.intlib.backend.execution.StructureException;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;

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
