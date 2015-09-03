/**
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
 */
package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;

/**
 * Add database possibility to joining tables.
 * 
 * @author Petyr
 * @param <T>
 */
public interface DbQueryBuilder<T extends DataObject>
        extends DataQueryBuilder<T, DbQuery<T>, DbQueryCount<T>>,
        DataQueryBuilder.Filterable<T, DbQuery<T>, DbQueryCount<T>>,
        DataQueryBuilder.Sortable<T, DbQuery<T>, DbQueryCount<T>> {

    /**
     * Add given property into the fetch list. Non-trivial classes in fetch list
     * will be loaded together with the main class instance {@code T}.
     * <b>The given property name must be name of direct non-trivial property of
     * main class {@code T}. </b>
     * 
     * @param propertyName
     *            Name of property to fetch.
     */
    void addFetch(String propertyName);

    /**
     * Remove given property from fetch list.
     * 
     * @param propertyName
     *            Name of property to remove from fetch list.
     */
    void removeFetch(String propertyName);

    /**
     * Clear fetch list.
     */
    void clearFetch();

}
