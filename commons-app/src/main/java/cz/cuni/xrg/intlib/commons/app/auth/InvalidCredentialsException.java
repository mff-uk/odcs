package cz.cuni.xrg.intlib.commons.app.auth;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception caused by invalid credentials provided by user.
 *
 * @author Jan Vojt
 */
public class InvalidCredentialsException extends AuthenticationException {

	/**
	 * Constructs an instance of
	 * <code>InvalidCredentialsException</code> with the specified detail
	 * message.
	 *
	 * @param msg the detail message.
	 */
	public InvalidCredentialsException(String username) {
		super(String.format("Invalid password or nonexistent username provided: ?", username));
	}
	
}
