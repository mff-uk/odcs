package cz.cuni.xrg.intlib.frontend;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Handles login and logout actions.
 *
 * @author Jan Vojt
 */
public class AuthenticationService {
	
	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authManager;
	
	@Autowired
	private LogoutHandler logoutHandler;
	
	public void login(String login, String password, HttpServletRequest httpRequest)
			throws AuthenticationException {

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);

		token.setDetails(new WebAuthenticationDetails(httpRequest));

		Authentication authentication = authManager.authenticate(token);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public void logout(HttpServletRequest httpRequest) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Response should not be used?
		logoutHandler.logout(httpRequest, null, authentication);
	}
}
