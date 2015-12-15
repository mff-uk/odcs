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

import javax.persistence.TypedQuery;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccess;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQuery;

/**
 * Query can be created by {@link cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder} and used in {@link DataAccess}.
 * 
 * @author Petyr
 * @param <T>
 */
public class DbQuery<T extends DataObject> implements DataQuery<T> {

    protected final TypedQuery<T> query;

    /**
     * Create new query.
     * 
     * @param query
     *            Inner query.
     */
    protected DbQuery(TypedQuery<T> query) {
        this.query = query;
    }

    TypedQuery<T> getQuery() {
        return query;
    }

    /**
     * Set limits for this query.
     * 
     * @param first
     *            Index of first required object.
     * @param count
     *            How many objects to return.
     * @return This database query.
     */
    public DbQuery<T> limit(int first, int count) {
        query.setFirstResult(first);
        query.setMaxResults(count);
        return this;
    }

}
