package cz.cuni.xrg.intlib.backend.dpu.event;

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
	
	public DPUFailedEvent(Exception ex, Context context, Object source) {
		super(context, source, MessageRecordType.DPU_ERROR, MESSAGE, ex);
	}	
	
}
