package cz.cuni.xrg.intlib.commons.app.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds user data (his account).
 *
 * @author Jiri Tomes
 */
public final class User implements RoleHolder, Resource {

    private int id;
    private String email;
    private String name;
    private String password;
    private List<Role> roles = new ArrayList<>();

    public User(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
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

    @Override
    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<Role> newRoles) {
        roles = newRoles;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getResourceId() {
        return User.class.toString();
    }
}
