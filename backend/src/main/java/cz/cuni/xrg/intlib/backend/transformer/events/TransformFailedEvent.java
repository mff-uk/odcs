package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Published when a {@link Transform} throws an exception during transformation.
 *
 * @author Petyr
 * 
 */
public class TransformFailedEvent extends TransformEvent {

    private final Exception exception;

    public TransformFailedEvent(Exception exception, Transform transformer, ExtendedTransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
        this.exception = exception;
    }

	@Override
	public MessageRecord getRecord() {		
		return new MessageRecord(time, MessageRecordType.DPU_ERROR, dpuInstance, execution, "Transform failed.", "Exception: " + exception.getMessage());
	}
	
}