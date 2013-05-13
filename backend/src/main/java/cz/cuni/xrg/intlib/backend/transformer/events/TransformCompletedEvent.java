package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Event is published when a {@link Transform} completed successfully.
 *
 * @author Petyr
 * @author Alex Kreiser (akreiser@gmail.com)
 * 
 */
public class TransformCompletedEvent extends TransformEvent {

    public TransformCompletedEvent(Transform transformer, ExtendedTransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
    }
    
	@Override
	public DPURecord getRecord() {		
		return new DPURecord(time, DPURecordType.INFO, dpuInstance, "Transform completed.", "");
	}    
}
