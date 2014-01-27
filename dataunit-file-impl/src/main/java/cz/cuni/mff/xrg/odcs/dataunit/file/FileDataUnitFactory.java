package cz.cuni.mff.xrg.odcs.dataunit.file;

import java.io.File;

/**
 * FileDataUnitFactory for creating {@Link FileDataUnit}s.
 *
 * @author Petyr
 */
public class FileDataUnitFactory {

	private FileDataUnitFactory() {

	}

	/**
	 * Create {@link FileDataUnit}.
	 * 
	 * @param name Name for newly created {@link FileDataUnit}.
	 * @param directory Directory where {@link FileDataUnit} can store it's data.
	 * @return 
	 */
	public static ManageableFileDataUnit create(final String name, 
			final File directory) {
		return new FileDataUnitImpl(name, directory);
	}

}