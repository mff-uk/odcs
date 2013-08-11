package cz.cuni.xrg.intlib.rdf.exceptions;

/**
 *
 * @author Jiri Tomes
 */
public class RDFDataUnitException extends Exception {

	public RDFDataUnitException() {
	}

	public RDFDataUnitException(String message) {
		super(message);
	}

	public RDFDataUnitException(Throwable cause) {
		super(cause);
	}

	public RDFDataUnitException(String message, Throwable cause) {
		super(message, cause);
	}
}
