package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordHash;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.NotificationRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.UserNotificationRecord;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for managing persisted User entities.
 *
 * @author Jan Vojt
 */
@Transactional(readOnly = true)
public class UserFacade {

	private static final Logger LOG = LoggerFactory.getLogger(UserFacade.class);
	
	/**
	 * Entity manager for accessing database with persisted objects.
	 */
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DbUser userDao;
	
	/**
	 * Factory for a new User.
	 * 
	 * @param username
	 * @param password
	 * @param email
	 * @return new user instance
	 */
	public User createUser(String username, String password, EmailAddress email) {
		
		String passHash = null;
		try {
			passHash = PasswordHash.createHash(password);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
			throw new RuntimeException("Could not hash user password.", ex);
		}
		
		User user = new User(username, passHash, email);
		
		// set default notification setting
		UserNotificationRecord notify = new UserNotificationRecord();
		user.setNotification(notify);
		
		notify.addEmail(email);
		notify.setTypeError(NotificationRecordType.INSTANT);
		notify.setTypeSuccess(NotificationRecordType.DAILY);
		
		return user;
	}
	
	/**
	 * @return list of all users persisted in database
	 */
	@PostFilter("hasPermission(filterObject, 'view')")
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}
	
	/**
	 * @param id primary key
	 * @return user with given id or <code>null<code>
	 */
	@PostAuthorize("hasPermission(returnObject, 'view')")
	public User getUser(long id) {
		return userDao.getInstance(id);
	}
	
	/**
	 * Find User by his unique username. This method is not secured, so that
	 * yet unauthenticated users can login.
	 * 
	 * @param username
	 * @return user
	 */
	public User getUserByUsername(String username) {
		User user = userDao.getByUsername(username);
		if (user == null) {
			LOG.info("User with username {} was not found.", username);
		}
		
		return user;
	}

	/**
	 * Saves any modifications made to the User into the database.
	 *
	 * @param user
	 */
	@Transactional
	@PreAuthorize("hasPermission(#user, 'save')")
	public void save(User user) {
		userDao.save(user);
	}

	/**
	 * Deletes pipeline from database.
	 *
	 * @param user
	 */
	@Transactional
	@PreAuthorize("hasPermission(#user, 'delete')")
	public void delete(User user) {
		userDao.delete(user);
	}

}
