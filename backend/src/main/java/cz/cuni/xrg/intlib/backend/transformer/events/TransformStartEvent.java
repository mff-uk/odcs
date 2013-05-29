package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 *
 * @see Extract
 * @author Petyr
 */
public class TransformStartEvent extends TransformEvent {

    public TransformStartEvent(Transform transformer, ExtendedTransformContext context, Object source) {
        super(transformer, context, source);
    }

	@Override
	public Record getRecord() {		
		return new Record(time, RecordType.DPUINFO, dpuInstance, execution, "Loader started.", "");
	}
}
