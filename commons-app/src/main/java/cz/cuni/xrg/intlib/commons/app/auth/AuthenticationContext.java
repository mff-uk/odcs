package cz.cuni.xrg.intlib.commons.app.auth;

import cz.cuni.xrg.intlib.commons.app.user.User;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Keeps authentication context for current user session.
 *
 * @author Jan Vojt
 */
public class AuthenticationContext implements ApplicationListener<AuthenticationSuccessEvent> {
	
	/**
	 * Retrieves username of currently logged in user from session.
	 * 
	 * @return username if user is authenticated, empty string otherwise
	 */
	public String getUsername() {
		Authentication auth = getAuth();
		return auth == null ? "" : auth.getName();
	}
	
	/**
	 * Retrieves currently authenticated user.
	 * 
	 * @return logged-in user
	 */
	public User getUser() {
		Authentication auth = getAuth();
		return auth == null ? null : (User) auth.getPrincipal();
	}
	
	/**
	 * Decides whether any user is currently successfully authenticated.
	 * Anonymous users are not considered authenticated.
	 * 
	 * @return authentication status
	 */
	public boolean isAuthenticated() {
		Authentication auth = getAuth();
		if (auth == null) {
			return false;
		}
		
		if (auth instanceof AnonymousAuthenticationToken) {
			return false;
		}
		
		return auth.isAuthenticated();
	}
	
	/**
	 * Clears all authentication data in user session. Use when logging out user.
	 */
	public void clear() {
		SecurityContextHolder.clearContext();
	}

	/**
	 * Successful authentication handler needs to setup {@link Authentication}
	 * in security context.
	 * 
	 * @param event successful authentication event
	 */
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		SecurityContextHolder.getContext().setAuthentication(event.getAuthentication());
	}
	
	/**
	 * Helper method for getting authentication from session context.
	 * 
	 * @return authentication
	 */
	private Authentication getAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
