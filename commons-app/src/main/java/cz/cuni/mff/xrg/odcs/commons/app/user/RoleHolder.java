package cz.cuni.mff.xrg.odcs.commons.app.user;

import java.util.Set;

/**
 * For managing system rights.
 *
 * @author Jiri Tomes
 */
public interface RoleHolder {

    public void addRole(Role role);

    public Set<Role> getRoles();

    public void setRoles(Set<Role> newRoles);
}
