package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 *
 * @author Jiri Tomes
 */
public class InvalidQueryException extends DataUnitException {

	private static final String message = "This SPARQL query is not valid !!!";

	public InvalidQueryException() {
		super(message);
	}

	public InvalidQueryException(String message) {
		super(message);
	}

	public InvalidQueryException(Throwable cause) {
		super(cause);
	}

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
