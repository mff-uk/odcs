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
package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQuery;

/**
 * Special query for logs.
 * 
 * @author Škoda Petr <skodapetr@gmail.com>
 */
class DbLogQuery extends DbQuery {

    final List<Object> filters;

    final List<String> fetchList;

    final String sortProperty;

    final Boolean sortAsc;

    Integer first;

    Integer count;

    DbLogQuery(List<Object> filters, Set<String> fetchList,
            String sortProperty, Boolean sortAsc) {
        super(null);
        this.filters = new ArrayList<>(filters);
        this.fetchList = new ArrayList<>(fetchList);
        this.sortProperty = sortProperty;
        this.sortAsc = sortAsc;
        this.first = null;
        this.count = null;
    }

    @Override
    public DbQuery limit(int first, int count) {
        this.first = first;
        this.count = count;
        return this;
    }

}
