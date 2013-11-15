package cz.cuni.mff.xrg.odcs.frontend.container;

import java.util.List;

/**
 * Interface for container self description.
 * 
 * @author Petyr
 *
 */
public interface ContainerDescription {

	/**
	 * Return ids of columns that are filterable. If there are no filters
	 * available then return empty List.
	 * @return
	 */
	public List<String> getFilterables();

	/**
	 * Return name for column of given id.
	 * @param id
	 * @return
	 */
	public String getColumnName(String id);
	
	/**
	 * Return ids of column that are visible.
	 * @return 
	 */
	public List<String> getVisibles();
	
}
