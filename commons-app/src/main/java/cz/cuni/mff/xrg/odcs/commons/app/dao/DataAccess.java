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
package cz.cuni.mff.xrg.odcs.commons.app.dao;

/**
 * Provide read and write access for given object type.
 * 
 * @author Petyr
 * @param <T>
 *            Data object.
 * @param <BUILDER>
 *            Query builder.
 * @param <QUERY>
 *            Query for list or single item.
 * @param <QUERY_SIZE>
 *            Query used for size.
 */
public interface DataAccess<T extends DataObject, BUILDER extends DataQueryBuilder<T, QUERY, QUERY_SIZE>, QUERY extends DataQuery<T>, QUERY_SIZE extends DataQueryCount<T>>
        extends DataAccessRead<T, BUILDER, QUERY, QUERY_SIZE> {

    /**
     * Persist given object into database.
     * 
     * @param object
     */
    public void save(T object);

    /**
     * Delete given object from database.
     * 
     * @param object
     */
    public void delete(T object);

}
