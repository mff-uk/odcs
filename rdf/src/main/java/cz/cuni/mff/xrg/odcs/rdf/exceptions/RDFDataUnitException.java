package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Exception is responsible for global problems related with RDF data unit.
 *
 * @author Jiri Tomes
 */
public class RDFDataUnitException extends DataUnitException {

	/**
	 * Create a new instance of {@link RDFDataUnitException} with empty default
	 * message.
	 */
	public RDFDataUnitException() {
		super("");
	}

	/**
	 * Create new instance of {@link RDFDataUnitException} with specific
	 * message.
	 *
	 * @param message String value of described message
	 */
	public RDFDataUnitException(String message) {
		super(message);
	}

	/**
	 * Create new instance of {@link RDFDataUnitException} with cause of
	 * throwing this exception.
	 *
	 * @param cause Cause of throwing exception
	 */
	public RDFDataUnitException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link RDFDataUnitException} with a specific
	 * message and cause of throwing this exception.
	 *
	 * @param message String value of described message
	 * @param cause   Cause of throwing exception
	 */
	public RDFDataUnitException(String message, Throwable cause) {
		super(message, cause);
	}
}
