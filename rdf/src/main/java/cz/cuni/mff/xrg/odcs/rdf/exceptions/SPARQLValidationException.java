package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;

/**
 * Exception is thrown when given SPARQL data are not valid using SPARQL
 * validator.
 *
 * @author Jiri Tomes
 */
public class SPARQLValidationException extends ConfigException {

	/**
	 * Create a new instance of {@link SPARQLValidationException} with default
	 * message.
	 */
	public SPARQLValidationException() {
	}

	/**
	 * Create new instance of {@link SPARQLValidationException} with specific
	 * message.
	 *
	 * @param message String value of described message
	 */
	public SPARQLValidationException(String message) {
		super(message);
	}

	/**
	 * Create new instance of {@link SPARQLValidationException} with cause of
	 * throwing this exception.
	 *
	 * @param cause Cause of throwing exception
	 */
	public SPARQLValidationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link SPARQLValidationException} with a specific
	 * message and cause of throwing this exception.
	 *
	 * @param message String value of described message
	 * @param cause   Cause of throwing exception
	 */
	public SPARQLValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
