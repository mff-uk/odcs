package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

/**
 * Provide additional functionality to manage the 
 * {@link cz.cuni.mff.xrg.odcs.dataunit.file.handlers.Handler}.
 * 
 * @author Petyr
 */
public interface ManageableHandler extends Handler {
		
	/**
	 * @return true if read only mode
	 */
	boolean isReadOnly();
	
	/**
	 * @param isReadOnly 
	 */
	void setReadOnly(boolean isReadOnly);
		
	/**
	 * @return True if this handler represents link to the existing file/directory.
	 */
	boolean isLink();
	
}
