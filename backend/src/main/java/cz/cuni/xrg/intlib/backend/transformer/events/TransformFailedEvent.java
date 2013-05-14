package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Published when a {@link Transform} throws an exception during transformation.
 *
 * @author Petyr
 * @author Alex Kreiser (akreiser@gmail.com)
 * 
 */
public class TransformFailedEvent extends TransformEvent {

    private final Exception exception;

    public TransformFailedEvent(Exception exception, Transform transformer, ExtendedTransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
        this.exception = exception;
    }

	@Override
	public Record getRecord() {		
		return new Record(time, RecordType.INFO, dpuInstance, "Transform failed.", "Exception: " + exception.getMessage());
	}
	
}