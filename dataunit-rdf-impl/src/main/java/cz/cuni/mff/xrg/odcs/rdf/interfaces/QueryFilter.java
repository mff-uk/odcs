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
package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryFilterManager;

/**
 * Interface responsible for filtering queries. Methods are using to managing
 * filters in class {@link QueryFilterManager}.
 * 
 * @author Jiri Tomes
 */
public interface QueryFilter {

    /**
     * Return string representation for name of filter.
     * 
     * @return name of filter.
     */
    public String getFilterName();

    /**
     * Return string representation of query transformed by filter.
     * 
     * @param originalQuery
     *            query as input to filter
     * @return transformed query using filter.
     */
    public String applyFilterToQuery(String originalQuery);
}
