/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.i18n;

import java.util.Locale;

/**
 * Holder for locale settings.
 * Uses en_US locale as default.
 *
 * @author mva
 */
public class LocaleHolder {

    private static Locale locale = Locale.forLanguageTag("en_US"); // default value

    /**
     * Set current locale.
     *
     * @param locale
     *            Locale
     */
    public static void setLocale(Locale locale) {
        LocaleHolder.locale = locale;
    }

    /**
     * Get current locale.
     *
     * @return Locale
     */
    public static Locale getLocale() {
        return LocaleHolder.locale;
    }
}
