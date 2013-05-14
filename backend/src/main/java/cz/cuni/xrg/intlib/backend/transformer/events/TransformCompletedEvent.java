package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
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
	public Record getRecord() {		
		return new Record(time, RecordType.DPUINFO, dpuInstance, execution, "Transform completed.", "");
	}    
}
