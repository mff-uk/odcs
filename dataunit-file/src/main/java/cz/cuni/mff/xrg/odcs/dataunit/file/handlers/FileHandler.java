package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException;

/**
 * Represent a file from {@link FileDataUnit}.
 * 
 * @author Petyr
 */
public interface FileHandler extends Handler {	
	
	/**
	 * Get file content as a string.
	 * 
	 * @return Null in case of error or nonexistence of file.
	 */
	String getContent();
	
	/**
	 * Set file content to given string.
	 * 
	 * @param newContent 
	 * @throws cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException 
	 */
	void setContent(String newContent) throws FileDataUnitException;
	
}
