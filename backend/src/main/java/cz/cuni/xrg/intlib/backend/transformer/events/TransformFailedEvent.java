package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
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
	public DPURecord getRecord() {		
		return new DPURecord(time, DPURecordType.INFO, dpuInstance, "Transform failed.", "Exception: " + exception.getMessage());
	}
	
}