package cz.cuni.mff.xrg.odcs.frontend.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Class responsible for retrieving internationalized messages.
 * 
 * @author mva
 */
public class Messages {

    public static final String BUNDLE_NAME = "messages";

    public static final ReloadableResourceBundleMessageSource MESSAGE_SOURCE = initializeResourceBundle();

    /**
     * Get the resource bundle string stored under key, formatted using {@link MessageFormat}.
     *
     * @param key
     *            resource bundle key
     * @param args
     *            parameters to formatting routine
     * @return formatted string, returns "!key!" when the value is not found in bundle
     */
    public static String getString(final String key, final Object... args) {
        try {
            return MESSAGE_SOURCE.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Initialize resource bundle.
     * 
     * @return
     */
    private static ReloadableResourceBundleMessageSource initializeResourceBundle() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setDefaultEncoding("UTF-8");
        ms.setBasename("classpath:" + BUNDLE_NAME);
        return ms;
    }
}
