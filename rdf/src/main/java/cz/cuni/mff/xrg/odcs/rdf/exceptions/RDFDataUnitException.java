package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 *
 * @author Jiri Tomes
 */
public class RDFDataUnitException extends DataUnitException {

	public RDFDataUnitException() {
		super("");
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
