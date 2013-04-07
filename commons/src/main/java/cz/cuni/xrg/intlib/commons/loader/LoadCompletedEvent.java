package cz.cuni.xrg.intlib.commons.loader;

/**
 * Event is published when a {@link Load} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadCompletedEvent extends LoadEvent {

    public LoadCompletedEvent(Load loader, LoadContext loadContext, Object source) {
        super(loader, loadContext, source);
    }
}