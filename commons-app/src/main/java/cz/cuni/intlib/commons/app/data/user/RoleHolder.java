package cz.cuni.intlib.commons.app.data.user;

import java.util.List;

/**
 * For managing system rights.
 *
 * @author Jiri Tomes
 */
public interface RoleHolder {

    public void addRole(Role role);

    public List<Role> getRoles();

    public void setRoles(List<Role> newRoles);
}
