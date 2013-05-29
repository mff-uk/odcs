package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;

/**
 * Base class for {@link Load} events.
 *
 * @see Load
 * @author Petyr
 * 
 */
public abstract class LoadEvent extends DPUEvent {

    protected final Load loader;
    
    protected final ExtendedLoadContext loadContext;

    public LoadEvent(Load loader, ExtendedLoadContext context, Object source) {
        super(source, context.getDPUInstance(), context.getPipelineExecution());
        this.loader = loader;
        this.loadContext = context;
    }

    /**
     * Returns the {@link Load} associated with this event.
     *
     * @return
     */
    public Load getLoader() {
        return loader;
    }

    /**
     * Returns the {@link ExtendedLoadContext} of this execution.
     *
     * @return
     */
    public ExtendedLoadContext getLoadContext() {
        return loadContext;
    }
}