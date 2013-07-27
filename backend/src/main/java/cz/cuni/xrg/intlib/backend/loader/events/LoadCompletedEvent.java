package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.loader.Load;

/**
 * Event is published when a {@link Load} completed successfully.
 *
 * @author Petyr
 * 
 */
public class LoadCompletedEvent extends LoadEvent {

    public LoadCompletedEvent(Load loader, ExtendedLoadContext loadContext, Object source) {
        super(loader, loadContext, source);
    }
    
	@Override
	public MessageRecord getRecord() {		
		return new MessageRecord(time, MessageRecordType.DPU_INFO, dpuInstance, execution, "Loader completed.", "");
	}     
}