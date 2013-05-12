package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;

/**
 * Base class for {@link Load} events.
 *
 * @see Load
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadEvent extends DPUEvent {

    protected final Load loader;
    
    protected final LoadContext loadContext;

    public LoadEvent(Load loader, LoadContext loadContext, Object source) {
        super(source);
        this.loader = loader;
        this.loadContext = loadContext;
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
    public LoadContext getLoadContext() {
        return loadContext;
    }
}