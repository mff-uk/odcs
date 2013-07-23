package cz.cuni.xrg.intlib.rdf.exceptions;

/**
 *
 * Exception is thrown when RDF operation (extract,transform, load) cause
 * problems - was not executed successfully.
 *
 * @author Jiri Tomes
 */
public class RDFException extends Exception {

	public RDFException() {
		super();
	}

	public RDFException(Throwable cause) {
		super(cause);
	}

	public RDFException(String message) {
		super(message);
	}

	public RDFException(String message, Throwable cause) {
		super(message, cause);
	}
}
