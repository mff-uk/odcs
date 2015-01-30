package cz.cuni.mff.xrg.odcs.backend.i18n;

import java.util.Locale;

public class LocaleHolder {

    private static Locale locale = Locale.forLanguageTag("en_US"); // default value

    public static void setLocale(Locale locale) {
        LocaleHolder.locale = locale;
    }

    public static Locale getLocale() {
        return LocaleHolder.locale;
    }
}
