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
package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryCount;

/**
 * Special query for logs.
 * 
 * @author Škoda Petr <skodapetr@gmail.com>
 */
class DbLogQueryCount extends DbQueryCount {

    final List<Object> filters;

    final List<String> fetchList;

    DbLogQueryCount(List<Object> filters, Set<String> fetchList) {
        super(null);
        this.filters = new ArrayList<>(filters);
        this.fetchList = new ArrayList<>(fetchList);
    }

    DbLogQueryCount(TypedQuery<Number> query) {
        super(query);
        this.filters = null;
        this.fetchList = null;
    }

}
