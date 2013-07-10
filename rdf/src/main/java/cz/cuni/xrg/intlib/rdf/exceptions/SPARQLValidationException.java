package cz.cuni.xrg.intlib.rdf.exceptions;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;

/**
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
