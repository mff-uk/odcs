package cz.cuni.mff.xrg.odcs.commons.app.facade;

/**
 * Facade that provides internationalized messages from resource bundle.
 * 
 * @author mva
 */
public interface MessagesFacade extends Facade {

    /**
     * Get the resource bundle string stored under key.
     *
     * @param key
     *            resource bundle key
     * @param args
     *            array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}", "{2,time}" within a message) if
     *            any.
     * @return formatted string, returns "!key!" when the value is not found in bundle
     */
    public String getString(final String key, final Object... args);

}
