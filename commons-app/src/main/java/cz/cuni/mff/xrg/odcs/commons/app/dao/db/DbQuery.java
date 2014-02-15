package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccess;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQuery;
import javax.persistence.TypedQuery;

/**
 * Query can be created by
 * {@link cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder} and used in
 * {@link DataAccess}.
 *
 * @author Petyr
 *
 * @param <T>
 */
public class DbQuery<T extends DataObject> implements DataQuery<T> {
    
	private final TypedQuery<T> query;
	
	/**
	 * Create new query.
	 * @param query
	 */
	DbQuery(TypedQuery<T> query) {
		this.query = query;		
	}
	
	TypedQuery<T> getQuery() {
		return query;
	}
	
	/**
	 * Set limits for this query.
	 * 
	 * @param first
	 * @param count
	 * @return database query
	 */
	public DbQuery<T> limit(int first, int count) {		
		query.setFirstResult(first);
		query.setMaxResults(count);
		return this;
	}
	
}
