package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadException;

/**
 * Published when a {@link Load} could not complete because an error occurred.
 *
 * @author Petyr
 * 
 */
public class LoadFailedEvent extends LoadEvent {

    private final LoadException exception;

    public LoadFailedEvent(LoadException exception, Load loader, ExtendedLoadContext loadContext, Object source) {
        super(loader, loadContext, source);
        this.exception = exception;
    }
    
	@Override
	public Record getRecord() {		
		return new Record(time, RecordType.DPU_ERROR, dpuInstance, execution, "Loader failed.", "Exception: " + exception.getMessage());
	}    
    
}