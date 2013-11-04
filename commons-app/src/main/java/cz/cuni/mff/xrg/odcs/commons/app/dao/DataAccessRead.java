package cz.cuni.mff.xrg.odcs.commons.app.dao;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryCount;

/**
 * Read only access to data of given type.
 * 
 * @author Petyr
 * 
 * @param <T>
 */
public interface DataAccessRead<T extends DataObject> {

	/**
	 * Return fully loaded instance of object with given id. This operation may
	 * be expensive! Use {@link #getLightInstance(long)} if you need data
	 * directly only from the class itself.
	 * 
	 * @param id
	 * @return
	 */
	public T getInstance(long id);

	/**
	 * Return light instance of given class. Only data from single table that
	 * corresponds for given class-object are loaded. The subobjects of given
	 * object will not be loaded. Use {@link #getInstance(long)} to get fully
	 * loaded object.
	 * 
	 * @param id
	 * @return
	 */
	public T getLightInstance(long id);

	/**
	 * Execute given query, interpret given result as single object and return
	 * it.
	 * 
	 * @param query
	 * @return
	 */
	public T execute(DbQuery<T> query);

	/**
	 * Execute given query and return result as list of objects.
	 * 
	 * @param query
	 * @return
	 */
	public List<T> executeList(DbQuery<T> query);

	/**
	 * Execute count query and return result.
	 * 
	 * @param query
	 * @return
	 */
	public long executeSize(DbQueryCount<T> query);

	/**
	 * Create query builder that can be used to create query for this access.
	 * 
	 * @return
	 */
	public DataQueryBuilder<T> createQueryBuilder();

}
