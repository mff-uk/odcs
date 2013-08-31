package cz.cuni.xrg.intlib.rdf.exceptions;

/**
 * 
 * @author Jiri Tomes
 */
public class InsertPartException extends RDFException {

	public InsertPartException() {
	}

	public InsertPartException(String message) {
		super(message);
	}

	public InsertPartException(Throwable cause) {
		super(cause);
	}

	public InsertPartException(String message, Throwable cause) {
		super(message, cause);
	}
}
