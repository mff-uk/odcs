package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordHash;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.UserNotificationRecord;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Holds user data (his account).
 *
 * @author Jiri Tomes
 */
@Entity
@Table(name = "usr_user")
public class User implements UserDetails, OwnedEntity, RoleHolder, Resource {

	/**
	 * Primary key for entity.
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usr_user")
	@SequenceGenerator(name = "seq_usr_user", allocationSize = 1)
    private Long id;
	
	/**
	 * User name used for login and as a unique identification of User.
	 */
	@Column
	private String username;
	
	/**
	 * User email.
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "email_id")
    private EmailAddress email;
	
	/**
	 * Full name.
	 */
	@Column(name = "full_name")
    private String fullName;
	
	/**
	 * Hashed password.
	 */
	@Column(name = "u_password")
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
	 * User notification settings used as a default for execution schedules.
	 * Overridden by specific settings in {@link ScheduleNotificationRecord}.
	 */
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private UserNotificationRecord notification;

	/**
	 * Empty constructor required by JPA.
	 */
	public User() {
	}

	/**
	 * Constructs entity from required attributes.
	 * 
	 * @param username user login
	 * @param password already hashed password
	 * @param email contact email
	 */	
    public User(String username, String password, EmailAddress email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
    public EmailAddress getEmail() {
        return email;
    }

    public void setEmail(EmailAddress newEmail) {
        email = newEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

	@Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
		try {
			this.password = PasswordHash.createHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
		}
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

	public UserNotificationRecord getNotification() {
		return notification;
	}

	public void setNotification(UserNotificationRecord notification) {
		this.notification = notification;
		notification.setUser(this);
	}
	
    @Override
    public String getResourceId() {
        return User.class.toString();
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>(getRoles());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public User getOwner() {
		return this;
	}
}
