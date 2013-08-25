package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Event is published when an DPU completed successfully.
 *
 * @author Petyr
 * 
 */
public final class DPUCompleteEvent extends DPUEvent {

	public DPUCompleteEvent(Context context, Object source) {
		super(context, source, MessageRecordType.DPU_INFO, "DPU completed.");
	}

}
