package cz.cuni.xrg.intlib.commons.app.user;

import org.springframework.security.core.GrantedAuthority;

/**
 * Set of roles in the system.
 *
 * @author Jiri Tomes
 */
public enum Role implements GrantedAuthority {

    USER, ADMINISTRATOR;

	@Override
	public String getAuthority() {
        return name();
	}
}
