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
package cz.cuni.mff.xrg.odcs.rdf.query.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryFilter;

/**
 * Class responsible for manage filters to given set query.
 * 
 * @author Jiri Tomes
 */
public class QueryFilterManager {

    private List<QueryFilter> filters = new LinkedList<>();

    private String query;

    /**
     * Create new instance of {@link QueryFilterManager} based on given SPARQL
     * query.
     * 
     * @param query
     *            String value of SPARQL query.
     */
    public QueryFilterManager(String query) {
        this.query = query;
    }

    /**
     * Add next filter to the filters collection if there are not yet.
     * 
     * @param filter
     *            instance of filter we can add for applying on query.
     */
    public void addFilter(QueryFilter filter) {
        if (!filters.contains(filter)) {
            filters.add(filter);
        }

    }

    /**
     * Remove given filter from filters collection, if contains.
     * 
     * @param filter
     *            instance of filter we can remove from filters collection.
     */
    public void removeFilter(QueryFilter filter) {
        List<QueryFilter> toRemove = new ArrayList<>();

        for (QueryFilter next : filters) {
            if (next.equals(filter)) {
                toRemove.add(next);
            }
        }
        if (!toRemove.isEmpty()) {
            filters.removeAll(toRemove);
        }
    }

    /**
     * Remove given filter from filters collection using filter name, if
     * contains.
     * 
     * @param filterName
     *            String value of filter name we can remove.
     */
    public void removeFilter(String filterName) {
        List<QueryFilter> toRemove = new ArrayList<>();

        for (QueryFilter next : filters) {
            if (filterName.equals(next.getFilterName())) {
                toRemove.add(next);
            }
        }
        if (!toRemove.isEmpty()) {
            filters.removeAll(toRemove);
        }
    }

    /**
     * Returns count of used filters.
     * 
     * @return count of used filters.
     */
    public int getFiltersCount() {
        return filters.size();
    }

    /**
     * Returns collection of used filters.
     * 
     * @return collection of used filters.
     */
    public List<QueryFilter> getFilters() {
        return filters;
    }

    /**
     * Returns SPARQL query without using filters.
     * 
     * @return query without using filters.
     */
    public String getOriginalQuery() {
        return query;
    }

    /**
     * Returns string value of query as applying of all given set filters to
     * original query.
     * 
     * @return query as applying of all given set filters.
     */
    public String getFilteredQuery() {
        String resultQuery = query;

        for (QueryFilter nextFilter : filters) {
            resultQuery = nextFilter.applyFilterToQuery(resultQuery);
        }

        return resultQuery;
    }
}
