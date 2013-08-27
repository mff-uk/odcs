package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Event is published when an DPU execution start.
 *
 * @author Petyr
 * 
 */
public final class DPUStartEvent extends DPUEvent {

	public static final String MESSAGE = "DPU started.";
	
	public DPUStartEvent(Context context, Object source) {
		super(context, source, MessageRecordType.DPU_INFO, MESSAGE);
	}		
	
}
