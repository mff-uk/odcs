package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Event is published when a {@link Transform} execution starts.
 *
 * @author Petyr
 */
public class TransformStartEvent extends TransformEvent {

    public TransformStartEvent(Transform transformer, ExtendedTransformContext context, Object source) {
        super(transformer, context, source);
    }

	@Override
	public MessageRecord getRecord() {		
		return new MessageRecord(time, MessageRecordType.DPU_INFO, dpuInstance, execution, "Transformer started.", "");
	}
}
