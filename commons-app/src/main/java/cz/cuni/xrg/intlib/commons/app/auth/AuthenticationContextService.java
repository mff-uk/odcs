package cz.cuni.xrg.intlib.commons.app.auth;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Keeps authentication context for current user session.
 *
 * @author Jan Vojt
 */
public class AuthenticationContextService implements ApplicationListener<AuthenticationSuccessEvent> {
	
	/**
	 * Retrieves username of currently logged in user from session.
	 * 
	 * @return username if user is authenticated, empty string otherwise
	 */
	public String getUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? "" : auth.getName();
	}
	
	/**
	 * Decides whether any user is currently successfully authenticated.
	 * 
	 * @return authentication status
	 */
	public boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? false : auth.isAuthenticated();
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

}
