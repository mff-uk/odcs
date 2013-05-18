package cz.cuni.xrg.intlib.backend.pipeline.events;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Event is published if the pipeline is terminated due the error in DPU.
 * 
 * @author Petyr
 *
 */
public class PipelineFailedEvent extends PipelineEvent {

	private String longMessage;
	
    public PipelineFailedEvent(String message, DPUInstance dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        this.longMessage = message;
    }

    public PipelineFailedEvent(Exception exception, DPUInstance dpuInstance, PipelineExecution pipelineExec, Object source) {
        super(dpuInstance, pipelineExec, source);
        // get trace message
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        
        this.longMessage = sw.toString();
    }    
    
    @Override
	public Record getRecord() {
    	return new Record(time, RecordType.PIPELINEERROR, dpuInstance, execution, 
    			"Pipeline execution failed.", "Pipeline execution terminated because of: " + longMessage);
	}
	
}
