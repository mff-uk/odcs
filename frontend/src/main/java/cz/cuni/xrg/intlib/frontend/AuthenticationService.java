package cz.cuni.xrg.intlib.frontend;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.atmosphere.util.FakeHttpSession;
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
	
	/**
	 * Attribute key for storing {@link Authentication} in HTTP session.
	 */
	public static final String SESSION_KEY = "authentication";
	
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
		
		HttpSession session = RequestHolder.getRequest().getSession();
		session.setAttribute(SESSION_KEY, authentication);
		
		if (session instanceof FakeHttpSession) {
			// We are servicing a PUSH request in a websocket connection, so we
			// also need to update the servlet session outside this connection.
			getServletSession(session).setAttribute(SESSION_KEY, authentication);
		}
		
		httpRequest.getSession().setAttribute(SESSION_KEY, authentication);

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
		RequestHolder.getRequest().getSession().removeAttribute(SESSION_KEY);
	}

	/**
	 * Get the servlet session, which differs from the session on websocket
	 * request object.
	 * 
	 * @param session fake session on PUSH request
	 * @return session used in HTTP servlet requests
	 */
	private HttpSession getServletSession(HttpSession session) {
		ServletContext ctx = session.getServletContext();
		Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) ctx.getAttribute(SessionHolder.ATTR_KEY);
		return sessionMap.get(session.getId());
	}
}
