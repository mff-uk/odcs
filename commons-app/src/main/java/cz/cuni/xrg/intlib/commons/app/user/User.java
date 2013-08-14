package cz.cuni.xrg.intlib.commons.app.user;

import cz.cuni.xrg.intlib.commons.app.scheduling.EmailAddress;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.scheduling.UserNotificationRecord;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
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
	@OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private UserNotificationRecord notification;

	/**
	 * Empty constructor required by JPA.
	 */
	public User() {
	}

	/**
	 * Constructs entity from required attributes.
	 * 
	 * @param fullName full name
	 * @param password already hashed password
	 * @param email contact email
	 */	
    public User(String fullName, String password, EmailAddress email) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
