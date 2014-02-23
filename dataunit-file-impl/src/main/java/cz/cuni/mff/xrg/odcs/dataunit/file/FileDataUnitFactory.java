package cz.cuni.mff.xrg.odcs.dataunit.file;

import java.io.File;

/**
 * Factory for {@link ManageableFileDataUnit}.
 *
 * @author Petyr
 */
public class FileDataUnitFactory {

	private FileDataUnitFactory() {

	}

	/**
	 * Create instance of ManageableFileDataUnit.
	 * @param name Name for newly created .
	 * @param directory Directory where the working data can be stored.
	 * @return New instance of {@link ManageableFileDataUnit}.
	 */
	public static ManageableFileDataUnit create(final String name, 
			final File directory) {
		return new FileDataUnitImpl(name, directory);
	}

}