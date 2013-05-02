package cz.cuni.xrg.intlib.backend.transformer.events;

import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Event is published when a {@link Transform} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformCompletedEvent extends TransformEvent {

    public TransformCompletedEvent(Transform transformer, TransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
    }
}
