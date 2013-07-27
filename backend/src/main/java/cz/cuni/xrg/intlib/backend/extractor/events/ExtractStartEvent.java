package cz.cuni.xrg.intlib.backend.extractor.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;

/**
 * Event is published when an {@link Extract} execution starts.
 *
 * @author Petyr
 */
public class ExtractStartEvent extends ExtractEvent {

    public ExtractStartEvent(Extract extractor, ExtendedExtractContext context, Object source) {
        super(extractor, context, source);
    }

	@Override
	public MessageRecord getRecord() {		
		return new MessageRecord(time, MessageRecordType.DPU_INFO, dpuInstance, execution, "Extractor started.", "");
	}
}
