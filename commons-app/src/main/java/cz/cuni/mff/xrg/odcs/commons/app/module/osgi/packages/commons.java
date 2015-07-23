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
package cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages;

/**
 * The list of packages exported from commons.
 * 
 * @author Petyr
 */
public class commons {

    /**
     * List of OSGI packages to export. Does not start nor end with separator.
     */
    public static final String PACKAGE_LIST =
            "org.slf4j.bridge;uses:=\"org.slf4j.spi,org.slf4j\";version=\"1.7.7\"," +
                    "org.apache.log4j;uses:=\"org.apache.log4j.spi,org.slf4j.spi,org.apache.log4j.helpers,org.slf4j,org.slf4j.helpers\";version=\"1.2.17\"," +
                    "org.apache.log4j.helpers;version=\"1.2.17\"," +
                    "org.apache.log4j.xml;uses:=\"javax.xml.parsers,org.apache.log4j.spi,org.w3c.dom\";version=\"1.2.17\"," +
                    "org.apache.log4j.spi;uses:=\"org.apache.log4j\";version=\"1.2.17\"," +
                    "org.slf4j;version=\"1.7.7\"," + // Added
                    "org.slf4j.spi;uses:=\"org.slf4j\";version=\"1.7.7\"," +
                    "org.slf4j.helpers;uses:=\"org.slf4j.spi,org.slf4j\";version=\"1.7.7\"," +
                    "org.slf4j;uses:=\"org.slf4j.helpers,org.slf4j.spi\";version=\"1.7.7\"";

}
