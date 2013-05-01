package cz.cuni.xrg.intlib.commons.loader;

import cz.cuni.xrg.intlib.commons.event.DPUEvent;

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
     * Returns the {@link LoadContext} of this execution.
     *
     * @return
     */
    public LoadContext getLoadContext() {
        return loadContext;
    }
}