package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQuery;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents Java Persistence Query.
 *
 * @author Jan Vojt
 * @param <T> object resulting from this query
 */
public class JPQLDbQuery<T extends DataObject> implements DataQuery<T> {
	
	private String query;

	private final Map<String, Object> parameters = new HashMap<>();

	public JPQLDbQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Set<Map.Entry<String, Object>> getParameters() {
		return Collections.unmodifiableSet(parameters.entrySet());
	}

	/**
	 * Sets named parameter for this query. If parameter already existed, it is
	 * replaced. Passing null as value removes the parameter.
	 * 
	 * @param name
	 * @param value
	 * @return the same query to allow method chaining
	 */
	public JPQLDbQuery<T> setParameter(String name, Object value) {
		if (value == null) {
			parameters.remove(name);
		} else {
			parameters.put(name, value);
		}
		return this;
	}
}
