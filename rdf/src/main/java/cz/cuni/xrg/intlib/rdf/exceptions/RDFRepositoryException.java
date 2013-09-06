package cz.cuni.xrg.intlib.rdf.exceptions;

import org.openrdf.repository.RepositoryException;

/**
 * Custom replacement for {@link RepositoryException} with identical behavior
 * and representation. The only difference is that this exception is runtime,
 * so we do not have to litter our code with try-catch blocks. In case this
 * exception is thrown, we usually still do not know what to do in the catch
 * block.
 *
 * @see {@link RepositoryException}
 * @author Jan Vojt
 */
public class RDFRepositoryException extends RuntimeException {

	/**
	 * Creates a new instance of <code>RDFRepositoryException</code> without
	 * detail message.
	 */
	public RDFRepositoryException() {
	}

	/**
	 * Constructs an instance of <code>RDFRepositoryException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public RDFRepositoryException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an instance of <code>RDFRepositoryException</code> with
	 * specified detail message and root cause.
	 * 
	 * @param message
	 * @param cause 
	 */
	public RDFRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an instance of <code>RDFRepositoryException</code> with
	 * specified root cause.
	 * 
	 * @param cause 
	 */
	public RDFRepositoryException(Throwable cause) {
		super(cause);
	}
	
}
