package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Event is published when a {@link Transform} completed successfully.
 *
 * @author Petyr
 * 
 */
public class TransformCompletedEvent extends TransformEvent {

    public TransformCompletedEvent(Transform transformer, ExtendedTransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
    }
    
	@Override
	public MessageRecord getRecord() {		
		return new MessageRecord(time, MessageRecordType.DPU_INFO, dpuInstance, execution, "Transform completed.", "");
	}    
}
