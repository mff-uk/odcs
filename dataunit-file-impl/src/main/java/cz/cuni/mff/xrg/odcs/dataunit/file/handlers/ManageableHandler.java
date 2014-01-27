package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

/**
 * Provide additional functionality to manage the {@link Handler}.
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
	 * @return 
	 */
	boolean isLink();
	
}
