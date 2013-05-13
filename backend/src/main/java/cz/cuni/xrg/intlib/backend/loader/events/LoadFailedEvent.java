package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadException;

/**
 * Published when a {@link Load} could not complete because an error occurred.
 *
 * @author Petyr
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadFailedEvent extends LoadEvent {

    private final LoadException exception;

    public LoadFailedEvent(LoadException exception, Load loader, ExtendedLoadContext loadContext, Object source) {
        super(loader, loadContext, source);
        this.exception = exception;
    }
    
	@Override
	public DPURecord getRecord() {		
		return new DPURecord(time, DPURecordType.INFO, dpuInstance, "Loader failed.", "Exception: " + exception.getMessage());
	}    
    
}