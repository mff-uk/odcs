package cz.cuni.xrg.intlib.backend.extractor.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;

/**
 * Event is published when an {@link Extract} completed successfully.
 *
 * @author Petyr
 * 
 */
public class ExtractCompletedEvent extends ExtractEvent {

    public ExtractCompletedEvent(Extract extractor, ExtendedExtractContext context, Object source) {
        super(extractor, context, source);
    }

	@Override
	public Record getRecord() {		
		return new Record(time, RecordType.DPU_INFO, dpuInstance, execution, "Extract completed.", "");
	}
}