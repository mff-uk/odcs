package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccess;

/**
 * Query can be created by {@link DatabaseQueryBuilder} and used in 
 * {@link DataAccess}.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public class DbQuery<T> {

	private static final Logger LOG = LoggerFactory.getLogger(DbQuery.class);
	
	private final Query query;
	
	/**
	 * Create new query.
	 * @param query
	 */
	DbQuery(Query query) {
		this.query = query;		
	}
	
	Query getQuery() {
		return query;
	}
	
	/**
	 * Set limits for this query.
	 * @param first
	 * @param count
	 * @return
	 */
	public DbQuery<T> limit(int first, int count) {
		
		LOG.trace("Setting limists {}, {}", first, count);
		
		query.setFirstResult(first);
		query.setMaxResults(count);
		return this;
	}
	
}
