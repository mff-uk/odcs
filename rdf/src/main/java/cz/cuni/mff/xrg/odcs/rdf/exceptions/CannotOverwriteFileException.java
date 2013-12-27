package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Exception is thrown when file where we load RDF data is protected for
 * overwritting.
 *
 * @author Jiri Tomes
 */
public class CannotOverwriteFileException extends DataUnitException {

	private static final String MESSAGE = "This file cannot be overwritten";

	/**
	 * Create a new instance of {@link CannotOverwriteFileException} with
	 * default {@link #MESSAGE}.
	 */
	public CannotOverwriteFileException() {
		super(MESSAGE);
	}

	/**
	 * Create new instance of {@link CannotOverwriteFileException} with specific
	 * message.
	 *
	 * @param message String value of described message
	 */
	public CannotOverwriteFileException(String message) {
		super(message);
	}

	/**
	 * Create new instance of {@link CannotOverwriteFileException} with cause of
	 * throwing this exception.
	 *
	 * @param cause Cause of throwing exception
	 */
	public CannotOverwriteFileException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link CannotOverwriteFileException} with a
	 * specific message and cause of throwing this exception.
	 *
	 * @param message String value of described message
	 * @param cause   Cause of throwing exception
	 */
	public CannotOverwriteFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
