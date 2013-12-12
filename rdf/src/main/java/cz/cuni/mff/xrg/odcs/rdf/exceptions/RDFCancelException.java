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

	public RDFCancelException(String msg) {
		super(msg);
	}

	public RDFCancelException(Throwable cause) {
		super(cause);
	}

	public RDFCancelException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
