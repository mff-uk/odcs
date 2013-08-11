package cz.cuni.xrg.intlib.commons.app.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * Holds user data (his account).
 *
 * @author Jiri Tomes
 */
@Entity
@Table(name = "usr_user")
public class User implements RoleHolder, Resource {

	/**
	 * Primary key for entity.
	 */
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * User email.
	 */
	@Column
    private String email;
	
	/**
	 * Full name.
	 */
	@Column(name = "full_name")
    private String name;
	
	/**
	 * Hashed password.
	 */
	@Column
    private String password;
	
	/**
	 * User roles representing sets of privileges.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "role_id")
	@Enumerated(EnumType.ORDINAL)
	@CollectionTable(name = "usr_user_role", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();

	/**
	 * Empty constructor required by JPA.
	 */
	public User() {
	}

	/**
	 * Constructs entity from required attributes.
	 * 
	 * @param name full name
	 * @param password already hashed password
	 * @param email contact email
	 */	
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
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(Set<Role> newRoles) {
        roles = newRoles;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getResourceId() {
        return User.class.toString();
    }
}
