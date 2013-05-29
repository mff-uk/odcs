package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
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
	public Record getRecord() {		
		return new Record(time, RecordType.DPUINFO, dpuInstance, execution, "Loader started.", "");
	}
}
