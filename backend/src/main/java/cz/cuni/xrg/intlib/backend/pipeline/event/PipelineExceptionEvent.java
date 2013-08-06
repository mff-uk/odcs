package cz.cuni.xrg.intlib.backend.pipeline.event;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Base class for pipeline error event that needs to work 
 * with exception stack trace.
 * 
 * @author Petyr
 *
 */
public abstract class PipelineExceptionEvent extends PipelineEvent {

	/**
	 * Stack trace message.
	 */
	protected String longMessage;

	public PipelineExceptionEvent(Exception exception,
			DPUInstanceRecord dpuInstance,
			PipelineExecution pipelineExec,
			Object source) {
		super(dpuInstance, pipelineExec, source);
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		this.longMessage = sw.toString();
	}	
	
}
