package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordHash;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.NotificationRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.UserNotificationRecord;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for managing persisted User entities.
 *
 * @author Jan Vojt
 */
public class UserFacade {

	private static final Logger LOG = LoggerFactory.getLogger(UserFacade.class);
	
	/**
	 * Entity manager for accessing database with persisted objects.
	 */
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * @return new user instance
	 */
	public User createUser(String fullname, String password, EmailAddress email) {
		
		String passHash = null;
		try {
			passHash = PasswordHash.createHash(password);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
			throw new RuntimeException("Could not hash user password.", ex);
		}
		
		User user = new User(fullname, passHash, email);
		
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

		@SuppressWarnings("unchecked")
		List<User> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM User e").getResultList(),
				User.class
		);

		return resultList;
	}
	
	/**
	 * @param id primary key
	 * @return user with given id or <code>null<code>
	 */
	@PostAuthorize("hasPermission(returnObject, 'view')")
	public User getUser(long id) {
		return em.find(User.class, id);
	}
	
	/**
	 * Find User by his unique username. This method is not secured, so that
	 * yet unauthenticated users can login.
	 * 
	 * @param username
	 * @return user
	 */
	public User getUserByUsername(String username) {
		Query q = em.createQuery("SELECT e FROM User e WHERE e.username = :uname")
				.setParameter("uname", username);
		
		User user = null;
		try {
			user = (User) q.getSingleResult();
		} catch (NoResultException ex) {
			LOG.info(String.format("User with username ? was not found.", username));
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
		if (user.getId() == null) {
			em.persist(user);
		} else {
			em.merge(user);
		}
	}

	/**
	 * Deletes pipeline from database.
	 *
	 * @param user
	 */
	@Transactional
	@PreAuthorize("hasPermission(#user, 'delete')")
	public void delete(User user) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		User p = user.getId() == null
			? user : getUser(user.getId());
		if (p != null) {			
			em.remove(p);
		} else {
			LOG.warn("User with ID " + user.getId() + " was not found and so cannot be deleted!");
		}
	}

}
