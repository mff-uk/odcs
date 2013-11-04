package cz.cuni.mff.xrg.odcs.commons.app.dao;

/**
 * Provide read and write access for given object type.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public interface DataAccess <T extends DataObject> extends DataAccessRead<T> {
	
	/**
	 * Create new object of given type. 
	 * @return
	 */
	public T create();
	
	/**
	 * Return deep copy of given object.
	 * @param object
	 * @return
	 */
	public T copy(T object);
	
	/**
	 * Persist given object into database.
	 * @param object
	 */
	public void save(T object);
	
	/**
	 * Delete given object from database.
	 * @param object
	 */
	public void delete(T object);
	
}
