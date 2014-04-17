package cz.cuni.mff.xrg.odcs.dataunit.file;

import java.io.File;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.*;

/**
 * Implementation of {@link DataUnit} that enable storing files.
 * 
 * All the files are stored and accessible by the {@link #getRootDir()} method.
 * See the {@link DirectoryHandler} class for more information about usage.
 *
 * Sample usage, assume that all the exception are declared as throws.
 * <pre>
 * {@code 
 * FileDataUnit dataUnit;
 * DirectoryHandler root = dataUnit.getRootDir();
 * // add subdirectory with file
 * root.addNewDirectory("myDir").addNewFile("myFile");
 * // examine the root
 * for (Handler handler : root) {
 *	final File path = handler.asFile();
 *	if (handler instanceof FileHandler) {
 *		// it's a file
 *		FileHandler file = (FileHandler)handler;
 *	} else if (handler instanceof DirectoryHandler) {
 *		// it's a directory
 *		DirectoryHandler file = (DirectoryHandler)handler;
 *	}
 * }
 * }
 * </pre>
 * 
 * For more samples please see {@link DirectoryHandler}, {@link FileHandler} and
 * {@link Handler}.
 * 
 * @author Petyr
 */
public interface FileDataUnit extends DataUnit {

	/**
	 * Return root {@link DirectoryHandler} that provides access to the
	 * all FileDataUnit's data.
	 * @return Handler to the root directory.
	 */
	DirectoryHandler getRootDir();
	void save(File directory);
	void load(File directory); 
}