package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;

/**
 *
 * Exception is thrown when RDF operation (extract,transform, load) cause
 * problems - was not executed successfully.
 *
 * @author Jiri Tomes
 */
public class RDFException extends RDFDataUnitException {

	/**
	 * Create a new instance of {@link RDFException} with empty default message.
	 */
	public RDFException() {
		super();
	}

	/**
	 * Create new instance of {@link RDFException} with cause of throwing this
	 * exception.
	 *
	 * @param cause Cause of throwing exception
	 */
	public RDFException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link RDFException} with specific message.
	 *
	 * @param message String value of described message
	 */
	public RDFException(String message) {
		super(message);
	}

	/**
	 * Create new instance of {@link RDFException} with a specific message and
	 * cause of throwing this exception.
	 *
	 * @param message String value of described message
	 * @param cause   Cause of throwing exception
	 */
	public RDFException(String message, Throwable cause) {
		super(message, cause);
	}
}
