package cz.cuni.xrg.intlib.rdf.exceptions;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 *
 * @author Jiri Tomes
 */
public class CannotOverwriteFileException extends DataUnitException {

	private static final String MESSAGE = "This file cannot be overwritten";

	public CannotOverwriteFileException() {
		super(MESSAGE);
	}

	public CannotOverwriteFileException(String message) {
		super(message);
	}

	public CannotOverwriteFileException(Throwable cause) {
		super(cause);
	}

	public CannotOverwriteFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
