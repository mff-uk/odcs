package cz.cuni.xrg.intlib.commons.app.scheduling;

/**
 * 
 * @author Jan Vojt
 */
public class MalformedEmailAddressException extends Exception {

	/**
	 * Creates a new instance of
	 * <code>MalformedEmailAddressException</code> without detail message.
	 */
	public MalformedEmailAddressException() {
	}

	/**
	 * Constructs an instance of
	 * <code>MalformedEmailAddressException</code> with the specified detail
	 * message.
	 *
	 * @param msg the detail message.
	 */
	public MalformedEmailAddressException(String email) {
		super(String.format("Invalid email syntax or invalid domain: %s.", email));
	}
}
