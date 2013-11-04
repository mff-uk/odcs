package cz.cuni.mff.xrg.odcs.commons.app.dao;

/**
 * Interface for objects that can be used with {@link DataAccess} and
 * {@link DataAccessRead}.
 * 
 * @author Petyr
 *
 */
public interface DataObject {

	/**
	 * Return object's id.
	 * @return
	 */
	public Long getId();
		
}
