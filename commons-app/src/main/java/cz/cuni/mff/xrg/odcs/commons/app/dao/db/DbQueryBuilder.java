package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;

/**
 * Add database possibility to joining tables.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public interface DbQueryBuilder<T> extends DataQueryBuilder<T>, 
    DataQueryBuilder.Filterable<T>, DataQueryBuilder.Sortable<T> {

	public DbQueryBuilder<T> joinLeft(Class<?> toWhich,
			String onPropertyName, Class<?> what);
	
	public DbQueryBuilder<T> joinLeftFetch(Class<?> toWhich,
			String onPropertyName, Class<?> what);
	
}
