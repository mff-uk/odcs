package cz.cuni.mff.xrg.odcs.rdf.exceptions;

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;

/**
 * Exception is thrown when during execution method
 * {@link RDFParser#parse(java.io.InputStream, java.lang.String)} to get RDF
 * data from SPARQL endpoint is this execution cancelled manually by user.
 *
 * @author Jiri Tomes
 */
public class RDFCancelException extends RDFHandlerException {

	/**
	 * Create new instance of {@link RDFCancelException} with specific message.
	 *
	 * @param msg String value of described message
	 */
	public RDFCancelException(String msg) {
		super(msg);
	}

	/**
	 * Create new instance of {@link RDFCancelException} with cause of throwing
	 * this exception.
	 *
	 * @param cause Cause of throwing exception
	 */
	public RDFCancelException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create new instance of {@link RDFCancelException} with a specific message
	 * and cause of throwing this exception.
	 *
	 * @param msg   String value of described message
	 * @param cause Cause of throwing exception
	 */
	public RDFCancelException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
