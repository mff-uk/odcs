package cz.cuni.mff.xrg.odcs.dataunit.rdf;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Base class for all RDF's exceptions.
 * 
 * @author Petyr
 */
public abstract class RDFDataUnitException extends DataUnitException {

	/**
	 * Create new instance of {@link RDFException} with cause of throwing this
	 * exception.
	 *
	 * @param cause Cause of throwing exception
	 */
	public RDFDataUnitException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link RDFException} with specific message.
	 *
	 * @param message String value of described message
	 */
	public RDFDataUnitException(String message) {
		super(message);
	}

	/**
	 * Create new instance of {@link RDFException} with a specific message and
	 * cause of throwing this exception.
	 *
	 * @param message String value of described message
	 * @param cause   Cause of throwing exception
	 */
	public RDFDataUnitException(String message, Throwable cause) {
		super(message, cause);
	}	
	
}
