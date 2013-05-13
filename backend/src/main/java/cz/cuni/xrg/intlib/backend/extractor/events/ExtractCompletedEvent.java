package cz.cuni.xrg.intlib.backend.extractor.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;

/**
 * Event is published when an {@link Extract} completed successfully.
 *
 * @author Petyr
 * @author Alex Kreiser (akreiser@gmail.com)
 * 
 */
public class ExtractCompletedEvent extends ExtractEvent {

    public ExtractCompletedEvent(Extract extractor, ExtendedExtractContext context, Object source) {
        super(extractor, context, source);
    }

	@Override
	public DPURecord getRecord() {		
		return new DPURecord(time, DPURecordType.INFO, dpuInstance, "Extract completed.", "");
	}
}