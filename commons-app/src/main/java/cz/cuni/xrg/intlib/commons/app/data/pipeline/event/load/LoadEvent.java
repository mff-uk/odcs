package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.load;

import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.ETLEvent;
import cz.cuni.xrg.intlib.commons.app.data.Loader;

/**
 * Base class for {@link Loader} events.
 *
 * @see Loader
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadEvent extends ETLEvent {

    protected final Loader loader;
    protected final LoadContext loadContext;

    public LoadEvent(Loader loader, LoadContext loadContext, Object source) {
        super(source);
        this.loader = loader;
        this.loadContext = loadContext;
    }

    /**
     * Returns the {@link Loader} associated with this event.
     *
     * @return
     */
    public Loader getLoader() {
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