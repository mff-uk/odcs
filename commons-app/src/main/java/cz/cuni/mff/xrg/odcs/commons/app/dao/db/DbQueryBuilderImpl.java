package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import cz.cuni.mff.xrg.odcs.commons.app.dao.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DbQueryBuilderImpl<T> implements DbQueryBuilder<T> {

    private final static Logger LOG = LoggerFactory.getLogger(DbQueryBuilderImpl.class);   
	/**
	 * Entity manager used to create query.
	 */
	private final EntityManager entityManager;

	/**
	 * 'From' part start with space, does not end with a whitespace.
	 */
	private final StringBuilder from = new StringBuilder();

	/**
	 * 'Where' part, if not empty then contains key word 'WHERE' and start but
	 * but not end with whitespace.
	 */
	private final StringBuilder where = new StringBuilder();

	/**
	 * 'Sort' part empty or container keyword 'sort' and start with whitespace.
	 */
	private final StringBuilder sort = new StringBuilder();

	/**
	 * Classes used in query.
	 */
	private final Set<Class<?>> usedClasses = new HashSet<>();

	/**
	 * Contains valued for used placeholders.
	 */
	private final Map<String, Object> placeHolderValues = new HashMap<>();

	/**
	 * Main class for query.
	 */
	private final Class<?> mainClass;

	/**
	 * Store cached query in string form.
	 */
	private String stringQueryCache = null;

	/**
	 * Store cached count query in string form.
	 */
	private String stringQueryCountCache = null;

	DbQueryBuilderImpl(EntityManager entityManager, Class<T> clazz) {
		this.entityManager = entityManager;
		// prepare initial FROM content
		from.append(" FROM ");
		from.append(clazz.getSimpleName());
		from.append(' ');
		from.append(clazz.getSimpleName().toLowerCase());
		// add to used classes
		usedClasses.add(clazz);
		// set main class
		mainClass = clazz;
	}

    @Override
	public DbQueryBuilder<T> joinLeft(Class<?> toWhich,
			String onPropertyName, Class<?> what) {
		if (usedClasses.contains(what)) {
			// joining twice over same class
			throw new RuntimeException("Multiple join over same table.");
		}

		if (!usedClasses.contains(toWhich)) {
			// joining over unknown table
			throw new RuntimeException("Joining to unknown table.");
		}

		// ad JOIN LEFT clause
		usedClasses.add(what);
		from.append(" LEFT JOIN ");
		from.append(toWhich.getSimpleName().toLowerCase());
		from.append('.');
		from.append(onPropertyName);
		from.append(' ');
		from.append(what.getSimpleName().toLowerCase());

		return this;
	}
	
    @Override
	public DbQueryBuilder<T> joinLeftFetch(Class<?> toWhich,
			String onPropertyName, Class<?> what) {
		if (usedClasses.contains(what)) {
			// joining twice over same class
			throw new RuntimeException("Multiple join over same table.");
		}

		if (!usedClasses.contains(toWhich)) {
			// joining over unknown table
			throw new RuntimeException("Joining to unknown table.");
		}

		// ad JOIN LEFT clause
		usedClasses.add(what);
		from.append(" LEFT JOIN FETCH ");
		from.append(toWhich.getSimpleName().toLowerCase());
		from.append('.');
		from.append(onPropertyName);
		from.append(' ');
		from.append(what.getSimpleName().toLowerCase());

		return this;
	}

    @Override
	public DbQueryBuilder<T> filterClear() {
		// delete where clause and placeHolders that are used in filters
		invalidateQueryCache();
		where.setLength(0);
		placeHolderValues.clear();
		return this;
	}

    @Override
	public DbQueryBuilder<T> filter(Class<?> clazz, FilterType type,
			Object value) {
		if (!usedClasses.contains(clazz)) {
			// joining over unknown table
			throw new RuntimeException("Usage of unknown table detected.");
		}
		invalidateQueryCache();
		// prepare place holder
		final String placeHolderName = 'p' + Integer.toString(placeHolderValues
				.size());
		placeHolderValues.put(placeHolderName, unwrapValue(value));
		// prepare query
		prepareWhereToAdd();
		where.append(clazz.getSimpleName().toLowerCase());
		where.append(' ');
		where.append(type.toString());
		where.append(" :");
		where.append(placeHolderName);
		return this;
	}

    @Override
	public DbQueryBuilder<T> filter(Class<?> clazz, String propertyName,
			FilterType type, Object value) {
		if (!usedClasses.contains(clazz)) {
			// joining over unknown table
			throw new RuntimeException("Usage of unknown table detected.");
		}
		invalidateQueryCache();
		// prepare place holder
		final String placeHolderName = 'p' + Integer.toString(placeHolderValues
				.size());
		placeHolderValues.put(placeHolderName, unwrapValue(value));
		// prepare query
		prepareWhereToAdd();
		where.append(clazz.getSimpleName().toLowerCase());
		where.append('.');
		where.append(propertyName);
		where.append(' ');
		where.append(type.toString());
		where.append(" :");
		where.append(placeHolderName);
		return this;
	}

    @Override
	public DbQueryBuilder<T> sort(Class<?> clazz, String propertyName,
			boolean asc) {
		invalidateQueryCache();

		// in any case delete old data
		sort.setLength(0);

		if (clazz == null) {
			return this;
		} else if (!usedClasses.contains(clazz)) {
			// joining over unknown table
			throw new RuntimeException("Usage of unknown table detected.");
		}

		// build order by clause
		sort.append(" ORDER BY ");
		sort.append(clazz.getSimpleName().toLowerCase());
		sort.append('.');
		sort.append(propertyName);
		if (asc) {
			sort.append(" ASC");
		} else {
			sort.append(" DESC");
		}
		return this;
	}

    @Override
	public DbQuery<T> getQuery() {
		if (stringQueryCache == null) {
			stringQueryCache = getStringQuery();
		}
		// create query and set placeholders
		Query query = entityManager.createQuery(stringQueryCache);
		setPlaceholders(query);
		return new DbQuery<T>(query);
	}

    @Override
	public DbQueryCount<T> getCountQuery() {
		if (stringQueryCountCache == null) {
			stringQueryCountCache = getStringQueryCount();
		}
		// create query and set placeholders
		Query query = entityManager.createQuery(stringQueryCountCache);
		setPlaceholders(query);
		return new DbQueryCount<T>(query);
	}

	/**
	 * Build query in string form.
	 * 
	 * @return
	 */
	String getStringQuery() {
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append("SELECT ");
		stringQuery.append(mainClass.getSimpleName().toLowerCase());
		stringQuery.append(from);
		stringQuery.append(where);
		stringQuery.append(sort);
        LOG.debug("Assembled query: {}", stringQuery.toString());
		return stringQuery.toString();
	}

	/**
	 * Builder count query in string form.
	 * 
	 * @return
	 */
	String getStringQueryCount() {
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append("SELECT Count(");
		stringQuery.append(mainClass.getSimpleName().toLowerCase());
		stringQuery.append(')');
		stringQuery.append(from);
		stringQuery.append(where);
		return stringQuery.toString();
	}

	/**
	 * Set placeholder for query.
	 * 
	 * @param query
	 */
	private void setPlaceholders(Query query) {
		for (String name : placeHolderValues.keySet()) {
			query.setParameter(name, placeHolderValues.get(name));
		}
	}

	/**
	 * Delete (invalid) cached data.
	 */
	private void invalidateQueryCache() {
		stringQueryCache = null;
		stringQueryCountCache = null;
	}

	/**
	 * Prepare {@link #where} for adding new expression.
	 */
	private void prepareWhereToAdd() {
		// prepare query
		if (where.length() == 0) {
			where.append(" WHERE ");
		} else {
			where.append(" AND ");
		}
	}
	
	/**
	 * Unwraps value to be used in a filter. If value is not wrapped, it is just
	 * returned as-is.
	 * 
	 * @param value wrapper
	 * @return unwrapped value
	 */
	private Object unwrapValue(Object value) {
		if (value instanceof ValuePostEvaluator) {
			return ((ValuePostEvaluator) value).evaluate();
		}
		return value;
	}

}
