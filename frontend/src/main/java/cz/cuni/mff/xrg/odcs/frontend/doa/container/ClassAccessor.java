package cz.cuni.mff.xrg.odcs.frontend.doa.container;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

/**
 * Describe class so it can be used in {@link ReadOnlyContainer}.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public interface ClassAccessor<T extends DataObject> {

	/**
	 * Return list of object's id.
	 * @return
	 */
	public List<String> all();
	
	/**
	 * Return subset of {@link #allIds()}. This subset will be enabled for
	 * sorting.
	 * @return
	 */
	public List<String> sortable();
	
	/**
	 * Return subset of {@link #allIds()}. This subset will be enabled for 
	 * filtering.
	 * @return
	 */
	public List<String> filterable();
	
	/**
	 * Return subset of {@link all()}. This subset will be visible.
	 * @return 
	 */
	public List<String> visible();
	
	/**
	 * List of properties to fetch.
	 * @return 
	 */
	public List<String> toFetch();
	
	/**
	 * Return entity class.
	 * @return
	 */
	public Class<T> getEntityClass();
	
	/**
	 * Return name of column for given id.
	 * @param id
	 * @return
	 */
	public String getColumnName(String id);
	
	/**
	 * Return value of object's given variable.
     * @param object
	 * @param id Variable identification.
	 * @return
	 */
	public Object getValue(T object, String id);
	
	/**
	 * Return type for given variable.
	 * @param id Variable identification.
	 * @return
	 */
	public Class<?> getType(String id);
}
