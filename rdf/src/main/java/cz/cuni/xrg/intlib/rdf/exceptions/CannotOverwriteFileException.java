package cz.cuni.xrg.intlib.rdf.exceptions;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 *
 * @author Jiri Tomes
 */
public class CannotOverwriteFileException extends DataUnitException {

	// private static final String MESSAGE = "This file cannot be overwritten";
	
	private final String message = "This file cannot be overwritten";

	// TODO Jirka(from Petyr): I do not undastand this .. why you 
	// override getMessage? Why you just don't set it in ctor?
	// ie. this is related to all exceptions .. 
	// may I ask you to revise this and update/send me email about it?
	
	public CannotOverwriteFileException() {
		super("");
		//super(MESSAGE);
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
