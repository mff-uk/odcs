package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.loader.Load;

/**
 * Event is published when an {@link Load} execution starts.
 * 
 * @author Petyr
 */
public class LoadStartEvent extends LoadEvent {

    public LoadStartEvent(Load loader, ExtendedLoadContext context, Object source) {
        super(loader, context, source);
    }

	@Override
	public MessageRecord getRecord() {		
		return new MessageRecord(time, MessageRecordType.DPU_INFO, dpuInstance, execution, "Loader started.", "");
	}
}
