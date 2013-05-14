package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Base class for {@link Transform} events
 *
 * @see Transform
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class TransformEvent extends DPUEvent {

    protected final Transform transformer;
    
    protected final ExtendedTransformContext transformContext;

    public TransformEvent(Transform transformer, ExtendedTransformContext context, Object source) {
        super(source, context.getDPUInstance(), context.getPipelineExecution());
        this.transformer = transformer;
        this.transformContext = context;
    }

    /**
     * Returns the {@link Transform} associated with this event.
     *
     * @return
     */
    public Transform getTransformer() {
        return transformer;
    }

    /**
     * Returns the {@link ExtendedTransformContext} of this execution.
     *
     * @return
     */
    public ExtendedTransformContext getTransformContext() {
        return transformContext;
    }
}
