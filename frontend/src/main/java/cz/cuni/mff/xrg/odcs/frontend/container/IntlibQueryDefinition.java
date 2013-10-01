package cz.cuni.mff.xrg.odcs.frontend.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

/**
 *
 * @author Bogo
 */
public class IntlibQueryDefinition<A> extends LazyQueryDefinition {

	private Object[] sortPropertyIds;
	private boolean[] ascendingStates;
	private Class<A> entityClass;
	private Map<Object, List<Filter>> filters = new HashMap<>();
	private int filterIndex = 0;

	public IntlibQueryDefinition(int batchSize, Class<A> entityClass) {
		super(false, batchSize, "id");
		this.entityClass = entityClass;
	}

	public void sort(Object[] sortPropertyIds, boolean[] ascendingStates) {
		this.sortPropertyIds = sortPropertyIds;
		this.ascendingStates = ascendingStates;
	}

	public void addContainerFilter(Object propertyId, String filterString,
			boolean ignoreCase, boolean onlyMatchPrefix) {
		getFilters(propertyId).add(
				new Filter(propertyId, filterString, ignoreCase,
				onlyMatchPrefix, filterIndex));
		filterIndex++;
	}

	public void removeAllContainerFilters() {
		filters.clear();
		filterIndex = 0;
	}

	public void removeContainerFilters(Object propertyId) {
		filters.remove(propertyId);
	}

	private List<Filter> getFilters(Object propertyId) {
		List<Filter> filterList = filters.get(propertyId);
		if (filterList == null) {
			filterList = new ArrayList<Filter>();
			filters.put(propertyId, filterList);
		}
		return filterList;
	}

	@Override
	public String toString() {
		String orderBy = createOrderByClause();

		String query = "SELECT p FROM " + entityClass.getSimpleName() + " AS p";

		String where = createWhereClause();
		if (where.length() > 0) {
			query += " WHERE " + where;
		}

		if (orderBy.length() > 0) {
			query += " ORDER BY " + orderBy;
		}

		return query;
	}

	public Map<String, Object> getParameterMap() {
		Map<String, Object> map = new HashMap<>();
		if (filters.size() > 0) {
			for (Object propertyId : filters.keySet()) {
				for (Filter filter : getFilters(propertyId)) {
					map.put(filter.getParamName(), filter.getParam());
				}
			}
		}
		return map;
	}

	public String createWhereClause() {
		String query = "";
		if (filters.size() > 0) {
			for (Object propertyId : filters.keySet()) {
				for (Filter filter : getFilters(propertyId)) {
					query += filter.toString();
					query += " AND ";
				}
			}

			if (query.length() > 5) {
				query = query.substring(0, query.length() - 5);
			}
		}

		return query;
	}

	private String createOrderByClause() {
		String orderBy = "";
		if (sortPropertyIds != null
				&& sortPropertyIds.length == ascendingStates.length) {
			for (int i = 0; i < sortPropertyIds.length; i++) {
				orderBy += "p." + sortPropertyIds[i].toString() + " ";
				orderBy += ascendingStates[i] ? "ASC" : "DESC";
				if (i < sortPropertyIds.length - 1) {
					orderBy += ", ";
				}

			}
		}

		return orderBy;
	}

	private static class Filter {

		private Object propertyId;
		private String filterString;
		private boolean ignoreCase;
		private boolean onlyMatchPrefix;
		private int index;

		public Filter(Object propertyId, String filterString,
				boolean ignoreCase, boolean onlyMatchPrefix, int index) {
			this.propertyId = propertyId;
			this.filterString = filterString;
			this.ignoreCase = ignoreCase;
			this.onlyMatchPrefix = onlyMatchPrefix;
		}

		@Override
		public String toString() {
			String str = "";
			if (ignoreCase) {
				str = "UPPER(p." + propertyId.toString() + ") LIKE UPPER(";
				str += ":param" + index + ")";
			} else {
				str = "p." + propertyId.toString() + " LIKE ";
				str += ":param" + index;
			}

			return str;
		}

		public String getParam() {
			String param = filterString + "%";
			if (!onlyMatchPrefix) {
				param = "%" + param;
			}

			return param;
		}

		private String getParamName() {
			return "param" + index;
		}
	}
}
