package cz.cuni.xrg.intlib.backend.dpu.event;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Event is published when an DPU execution failed.
 *
 * @author Petyr
 * 
 */
public final class DPUFailedEvent extends DPUEvent {
	
	public static final String MESSAGE = "DPU failed.";
	
	public DPUFailedEvent(Exception exception, Context context, Object source) {
		super(context, source, MessageRecordType.DPU_ERROR, MESSAGE);
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		this.longMessage = sw.toString();		
	}	
	
}
