package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException;

/**
 * Represent a file from {@link FileDataUnit}. The file it self can be 
 * accessed by {@link #asFile()} function. 
 * 
 * The other way is to use build in function {@link #getContent()} and
 * {@link #setContent(java.lang.String)} to read and write given string
 * into the file.
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
	 * @param newContent New file content.
	 * @throws cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException 
	 */
	void setContent(String newContent) throws FileDataUnitException;
	
}
