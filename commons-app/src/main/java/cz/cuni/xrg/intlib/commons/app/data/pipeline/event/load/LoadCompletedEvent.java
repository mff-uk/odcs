package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.load;

/**
 * Event is published when a {@link Loader} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadCompletedEvent extends LoadEvent {

    public LoadCompletedEvent(Loader loader, LoadContext loadContext, Object source) {
        super(loader, loadContext, source);
    }
}