package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Exception that is used to report problem during file creation.
 * @author Petyr
 */
public class FileCreationException extends DataUnitException {

	public FileCreationException(Throwable cause) {
		super(cause);
	}

	public FileCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileCreationException(String message) {
		super(message);
	}
	
}
