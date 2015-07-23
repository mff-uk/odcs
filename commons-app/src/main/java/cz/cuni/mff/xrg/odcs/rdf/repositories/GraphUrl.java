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
package cz.cuni.mff.xrg.odcs.rdf.repositories;

/**
 * Help class for creating graph's url.
 * 
 * @author Petyr
 * @author Jiri Tomes
 */
public class GraphUrl {

    private static final String prefix = "http://unifiedviews.eu/resource/internal/dataunit/";

    private GraphUrl() {
    }

    /**
     * Translate the dataUnit id in to graph URL format.
     * 
     * @param dataUnitId
     *            string value of data unit ID.
     * @return string representation of graph URL format.
     */
    public static String translateDataUnitId(String dataUnitId) {
        return prefix + dataUnitId.replace('_', '/');
    }

    /**
     * Return defined graph prefix used for application graph names.
     * 
     * @return defined graph prefix used for application graph names.
     */
    public static String getGraphPrefix() {
        return prefix;
    }
}
