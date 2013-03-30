package cz.cuni.intlib.commons.app.data.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds user data (his accounout).
 *
 * @author Jiri Tomes
 */
public final class Account implements RoleHolder, cz.cuni.intlib.commons.app.data.Resource {

    private String ID;
    private String email;
    private String name;
    private String password;
    private List<Role> roles = new ArrayList<Role>();

    public Account(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.ID = createUniqueID();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        email = newEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> newRoles) {
        roles = newRoles;
    }

    public String getID() {
        return ID;
    }

    /*
     * TODO - IMPLEMENT
     */
    public String createUniqueID() {
        return "ACOUNT_UNIQUE_ID";
    }
}
