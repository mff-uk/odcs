package cz.cuni.xrg.intlib.rdf.exceptions;

import java.io.IOException;

/**
 *
 * @author Jiri Tomes
 */
public class CannotOverwriteFileException extends IOException {

	private final String message = "This file cannot be overwritten";

	public CannotOverwriteFileException() {
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

	@Override
	public String getMessage() {
		return message;
	}
}
