package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;

/**
 * Add database possibility to joining tables.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public interface DbQueryBuilder<T extends DataObject> 
	extends DataQueryBuilder<T, DbQuery<T>, DbQueryCount<T>>, 
    DataQueryBuilder.Filterable<T, DbQuery<T>, DbQueryCount<T>>,
	DataQueryBuilder.Sortable<T, DbQuery<T>, DbQueryCount<T>> {
	
	/**
	 * Add given property into the fetch list. Non-trivial classes
	 * in fetch list will be loaded together with the main class instance {@link T}.
	 * 
	 * <b>The given property name must be name of direct non-trivial property
	 * of main class {@link T}. </b>
	 * 
	 * @param propertyName 
	 */
	void addFetch(String propertyName);
	
	/**
	 * Remove given property from fetch list.
	 * 
	 * @param propertyName 
	 */
	void removeFetch(String propertyName);
	
	/**
	 * Clear fetch list.
	 */
	void clearFetch();
	
}
