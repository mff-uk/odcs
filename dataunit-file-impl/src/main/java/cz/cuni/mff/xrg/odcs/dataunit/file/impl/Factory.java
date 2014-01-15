package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import java.io.File;

/**
 * Factory for creating {@Link FileDataUnit}s.
 *
 * @author Petyr
 */
public class Factory {

	private Factory() {

	}

	/**
	 * Create {@link FileDataUnit}.
	 * @param name
	 * @param directory
	 * @return 
	 */
	public static ManageableFileDataUnit create(final String name, final File directory) {
		return new FileDataUnitImpl(name, directory);
	}

}
