package cz.cuni.mff.xrg.odcs.dataunit.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import java.io.File;

/**
 * Handler for single file in {@link FileDataUnit}.
 * @author Petyr
 */
public interface FileHandler {
	
	/**
	 * Return path to file in {@link FileDataUnit}. This can be used to access
	 * even read only file. It's up to the programmer to not change the input
	 * data units.
	 * @return 
	 */
	File asFile();
	
	/**
	 * Return content of given file as string. In case of error return null.
	 * @return 
	 */
	String asString();
	
	/**
	 * Save given string into file.Overwrite any previous content.
	 * @param content 
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException 
	 */
	void setContent(final String content) throws DataUnitException;

	/**
	 * Return file name that has been used to create this represented file.
	 * @return 
	 */
	String getName();
	
}
