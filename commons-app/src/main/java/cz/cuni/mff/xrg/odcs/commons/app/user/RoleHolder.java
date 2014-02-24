package cz.cuni.mff.xrg.odcs.commons.app.user;

import java.util.Set;

/**
 * For managing system rights.
 *
 * @author Jiri Tomes
 */
public interface RoleHolder {

	/**
	 * Add the role to the role set.
	 *
	 * @param role The value of {@link Role} will be added.
	 */
	public void addRole(Role role);

	/**
	 * Returns the set of roles.
	 *
	 * @return the set of roles.
	 */
	public Set<Role> getRoles();

	/**
	 * Set the set of roles.
	 *
	 * @param newRoles the set of roles will be set.
	 */
	public void setRoles(Set<Role> newRoles);
}
