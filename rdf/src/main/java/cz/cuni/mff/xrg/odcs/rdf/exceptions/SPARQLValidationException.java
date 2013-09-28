package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;

/**
 * Exception is thrown when given SPARQL data are not valid using SPARQL
 * validator.
 *
 * @author Jiri Tomes
 */
public class SPARQLValidationException extends ConfigException {

	public SPARQLValidationException() {
	}

	public SPARQLValidationException(String message) {
		super(message);
	}

	public SPARQLValidationException(Throwable cause) {
		super(cause);
	}

	public SPARQLValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
