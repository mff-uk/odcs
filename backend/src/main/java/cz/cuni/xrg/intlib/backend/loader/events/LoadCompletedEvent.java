package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.loader.Load;

/**
 * Event is published when a {@link Load} completed successfully.
 *
 * @author Petyr
 * @author Alex Kreiser (akreiser@gmail.com)
 * 
 */
public class LoadCompletedEvent extends LoadEvent {

    public LoadCompletedEvent(Load loader, ExtendedLoadContext loadContext, Object source) {
        super(loader, loadContext, source);
    }
    
	@Override
	public Record getRecord() {		
		return new Record(time, RecordType.INFO, dpuInstance, "Loader completed.", "");
	}     
}