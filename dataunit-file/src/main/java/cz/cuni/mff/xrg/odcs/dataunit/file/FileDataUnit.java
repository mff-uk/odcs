package cz.cuni.mff.xrg.odcs.dataunit.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

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
	 * @return Can be null. 
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException 
	 */
	FileHandler add(final String name)throws DataUnitException;
	
	/**
	 * Remove given file from {@link FileDataUnit}. If the {@link FileDataUnit}
	 * is input, then nothing happened.
	 * @param handler 
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException 
	 */
	void delete(final FileHandler handler) throws DataUnitException;

}
