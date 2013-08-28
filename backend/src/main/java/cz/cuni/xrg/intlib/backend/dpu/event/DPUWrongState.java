package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Used to announced that given DPU is in unexpected state.
 * 
 * @author Petyr
 *
 */
public class DPUWrongState extends DPUEvent {

	public DPUWrongState(Context context, Object source, 
			String shortMessage, String longMessage) {
		super(context, source, MessageRecordType.PIPELINE_ERROR, 
				shortMessage, longMessage);
	}	
	
}
