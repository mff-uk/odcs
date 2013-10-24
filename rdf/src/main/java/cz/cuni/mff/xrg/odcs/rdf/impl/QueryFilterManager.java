package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class responsible for manage filters to given set query.
 *
 * @author Jiri Tomes
 */
public class QueryFilterManager {

	private List<QueryFilter> filters = new LinkedList<>();

	private String query;

	public QueryFilterManager(String query) {
		this.query = query;
	}

	/**
	 * Add next filter to the filters collection if there are not yet.
	 *
	 * @param filter instance of filter we can add for applying.
	 */
	public void addFilter(QueryFilter filter) {
		if (!filters.contains(filter)) {
			filters.add(filter);
		}

	}

	/**
	 * Remove given filter from filters collection, if contains.
	 *
	 * @param filter instance of filter we can remove from filters collection.
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
	 * @param filterName String value of filter name we can remove.
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
	 *
	 * @return count of used filters.
	 */
	public int getFiltersCount() {
		return filters.size();
	}

	/**
	 *
	 * @return collection of used filters.
	 */
	public List<QueryFilter> getFilters() {
		return filters;
	}

	/**
	 *
	 * @return query without using filters.
	 */
	public String getOriginalQuery() {
		return query;
	}

	/**
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
