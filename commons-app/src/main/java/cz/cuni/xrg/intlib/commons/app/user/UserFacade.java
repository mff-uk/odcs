package cz.cuni.xrg.intlib.commons.app.user;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	 * @return list of all users persisted in database
	 */
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
	public User getUser(long id) {
		return em.find(User.class, id);
	}

	/**
	 * Saves any modifications made to the User into the database.
	 *
	 * @param user
	 */
	@Transactional
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
