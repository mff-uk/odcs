package cz.cuni.mff.xrg.odcs.commons.app.auth;

import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import static cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordHash.validatePassword;

/**
 * Provider for custom authentication logic. Uses custom password hashing
 * provided by {@link PasswordHash}.
 *
 * @author Jan Vojt
 */
public class IntlibAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(IntlibAuthenticationProvider.class);
	
	private final UserFacade userFacade;

	/**
	 * Constructor sets up dependencies.
	 * 
	 * @param userFacade 
	 */
	public IntlibAuthenticationProvider(UserFacade userFacade) {
		this.userFacade = userFacade;
	}
	
	/**
	 * Authentication logic.
	 * 
	 * @param userDetails user to validate password against
	 * @param authentication token with password to validate
	 * @throws AuthenticationException 
	 */
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
		UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
		String password = (String) authentication.getCredentials();
		
		try {
			if (!validatePassword(password, userDetails.getPassword())) {
				throw new BadCredentialsException("Invalid username and/or password.");
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new RuntimeException("Could not generate password hash while authenticating user.", ex);
		}
	}

	/**
	 * Fetches user by his username to be tried for authentication.
	 * 
	 * @param username
	 * @param authentication
	 * @return
	 * @throws AuthenticationException 
	 */
	@Override
	protected UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
		User user = userFacade.getUserByUsername(authentication.getName());
		if (user == null) {
			throw new BadCredentialsException(username);
		}
		
		return user;
	}

}