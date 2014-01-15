package cz.cuni.mff.xrg.odcs.dataunit.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import java.io.File;

/**
 * Implementation of {@link DataUnit} that enable storing files.
 * @author Petyr
 */
public interface FileDataUnit extends DataUnit, Iterable<FileHandler> {
	
	/**
	 * Add file of given name into {@link FileDataUnit} and return handler to it.
	 * If the {@link FileDataUnit} is input, then nothing happened and null is 
	 * returned.
	 * @param name
	 * @param create If false the file is not physically created.
	 * @return Can be null. 
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException 
	 */
	FileHandler create(final String name, boolean create) throws DataUnitException;
	
	/**
	 * Add given existing file to the {@link FileDataUnit}.
	 * @param file
	 * @param asLink True if add as link, false to copy the file.
	 * @return
	 * @throws DataUnitException 
	 */
	FileHandler add(File file, boolean asLink) throws DataUnitException;
	
	/**
	 * Remove given file from {@link FileDataUnit}. If the {@link FileDataUnit}
	 * is input, then nothing happened.
	 * @param handler 
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException 
	 */
	void delete(final FileHandler handler) throws DataUnitException;

}
