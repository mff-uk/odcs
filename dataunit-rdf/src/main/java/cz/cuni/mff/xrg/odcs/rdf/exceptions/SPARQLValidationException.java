package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;

/**
 * Exception is thrown when SPARQL data are not valid using SPARQL validator.
 *
 * @author Jiri Tomes
 */
public class SPARQLValidationException extends ConfigException {

	/**
	 * Create a new instance of {@link SPARQLValidationException} with the
	 * default message.
	 */
	public SPARQLValidationException() {
	}

	/**
	 * Create new instance of {@link SPARQLValidationException} with the
	 * specific message.
	 *
	 * @param message String value of described message
	 */
	public SPARQLValidationException(String message) {
		super(message);
	}

	/**
	 * Create new instance of {@link SPARQLValidationException} with the cause
	 * of throwing this exception.
	 *
	 * @param cause The cause of throwing exception
	 */
	public SPARQLValidationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link SPARQLValidationException} with the
	 * specific message and the cause of throwing this exception.
	 *
	 * @param message String value of described message
	 * @param cause   The cause of throwing exception
	 */
	public SPARQLValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
