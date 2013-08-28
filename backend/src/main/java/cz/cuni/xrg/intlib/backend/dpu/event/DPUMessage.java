package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Class for representing DPURecord messages send by ProcessingContext
 * sendMessage.
 * 
 * @author Petyr
 * 
 */
public final class DPUMessage extends DPUEvent {

	public DPUMessage(String shortMessage,
			String longMessage,
			MessageType type,
			Context context,
			Object source) {
		super(context, source, MessageRecordType.fromMessageType(type),
				shortMessage, longMessage);
	}

}
