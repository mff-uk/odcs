package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.load;

import cz.cuni.xrg.intlib.commons.app.data.Loader;
/**
 * Published when a {@link Loader} could not complete because an error occurred.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadFailedEvent extends LoadEvent {

    private final LoadException exception;

    public LoadFailedEvent(LoadException exception, Loader loader, LoadContext loadContext, Object source) {
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