package cz.cuni.xrg.intlib.frontend;

import com.vaadin.server.VaadinSession;
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
 * Handles login and logout actions in frontend application.
 *
 * @author Jan Vojt
 */
public class AuthenticationService {
	
	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authManager;
	
	@Autowired
	private LogoutHandler logoutHandler;
	
	/**
	 * Creates security context and saves authentication details into session.
	 * 
	 * @param login
	 * @param password
	 * @param httpRequest
	 * @throws AuthenticationException 
	 */
	public void login(String login, String password, HttpServletRequest httpRequest)
			throws AuthenticationException {

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);

		token.setDetails(new WebAuthenticationDetails(httpRequest));

		Authentication authentication = authManager.authenticate(token);
		
		VaadinSession.getCurrent().setAttribute(Authentication.class, authentication);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * Clears security context and removes authentication from session.
	 * 
	 * @param httpRequest 
	 */
	public void logout(HttpServletRequest httpRequest) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		logoutHandler.logout(httpRequest, null, authentication);

		// clear session
		VaadinSession.getCurrent().setAttribute(Authentication.class, null);
	}
}
