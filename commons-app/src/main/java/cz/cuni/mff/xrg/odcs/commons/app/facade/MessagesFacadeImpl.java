package cz.cuni.mff.xrg.odcs.commons.app.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Facade that provides internationalized messages from resource bundle.
 * 
 * @author mva
 */
public class MessagesFacadeImpl implements MessagesFacade {

    @Autowired
    private MessageSource messageSource;

    /**
     * Get the resource bundle string stored under key.
     * Locale is retrieved from {@link org.springframework.context.i18n.LocaleContextHolder}.
     *
     * @param key
     *            resource bundle key
     * @param args
     *            array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}", "{2,time}" within a message) if
     *            any.
     * @return formatted string, returns "!key!" when the value is not found in bundle
     */
    @Override
    public String getString(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return '!' + key + '!';
        }
    }
}
