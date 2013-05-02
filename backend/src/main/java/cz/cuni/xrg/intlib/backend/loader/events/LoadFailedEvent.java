package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;

/**
 * Published when a {@link Load} could not complete because an error occurred.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadFailedEvent extends LoadEvent {

    private final LoadException exception;

    public LoadFailedEvent(LoadException exception, Load loader, LoadContext loadContext, Object source) {
        super(loader, loadContext, source);
        this.exception = exception;
    }

    /**
     * Returns the exception responsible for the failure
     *
     * @return
     */
    public LoadException getException() {
        return exception;
    }
}