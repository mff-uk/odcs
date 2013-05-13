package cz.cuni.xrg.intlib.backend.extractor.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;

/**
 * Event is published when an {@link Extract} could not complete because an error occurred.
 *
 * @author Petyr
 * @author Alex Kreiser (akreiser@gmail.com)
 * 
 */
public class ExtractFailedEvent extends ExtractEvent {

    private final ExtractException exception;

    public ExtractFailedEvent(ExtractException exception, Extract extractor, ExtendedExtractContext context, Object source) {
        super(extractor, context, source);
        this.exception = exception;
    }

	@Override
	public DPURecord getRecord() {		
		return new DPURecord(time, DPURecordType.INFO, dpuInstance, "Extract failed.", "Exception: " + exception.getMessage());
	}
	
}