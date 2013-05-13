package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
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
	public DPURecord getRecord() {		
		return new DPURecord(time, DPURecordType.INFO, dpuInstance, "Loader completed.", "");
	}     
}