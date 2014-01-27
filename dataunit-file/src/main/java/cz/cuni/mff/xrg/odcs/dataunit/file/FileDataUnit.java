package cz.cuni.mff.xrg.odcs.dataunit.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;

/**
 * Implementation of {@link DataUnit} that enable storing files.
 * 
 * All the files are stored and accessible by the {@link #getRootDir()} method.
 * See the {@link DirectoryHandler} class for more information about usage.
 *
 * @author Petyr
 */
public interface FileDataUnit extends DataUnit {

	/**
	 * Return access to the root directory.
	 * 
	 * @return 
	 */
	DirectoryHandler getRootDir();

}
