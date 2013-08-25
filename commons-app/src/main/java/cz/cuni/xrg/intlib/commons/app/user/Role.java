package cz.cuni.xrg.intlib.commons.app.user;

import org.springframework.security.core.GrantedAuthority;

/**
 * Set of roles in the system.
 *
 * @author Jiri Tomes
 */
public enum Role implements GrantedAuthority {

    ROLE_USER("User"),
	ROLE_ADMIN("Administrator");
	
	/**
	 * Human-readable string representation of role.
	 */
	private String role;

	private Role(String role) {
		this.role = role;
	}
	
	@Override
	public String getAuthority() {
        return name();
	}

	@Override
	public String toString() {
		return role;
	}
	
}
