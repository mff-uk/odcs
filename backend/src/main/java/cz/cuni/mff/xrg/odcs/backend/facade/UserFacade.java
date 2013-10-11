package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Facade for managing users, which tolerates database crashes. This facade
 * is specially altered for servicing backend, where we do not want to trash
 * all progress of unfinished pipeline runs just because of a short database
 * outage.
 *
 * <p>
 * TODO The concept of crash-proof facades could be solved nicer and with less
 *		code using AOP.
 * 
 * @author Jan Vojt
 */
public class UserFacade extends cz.cuni.mff.xrg.odcs.commons.app.user.UserFacade {
	
	/**
	 * Handler taking care of DB outages.
	 */
	@Autowired
	private ErrorHandler handler;

	@Override
	public List<User> getAllUsers() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllUsers();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public User getUser(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getUser(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public User getUserByUsername(String username) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getUserByUsername(username);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void save(User user) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(user);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void delete(User user) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(user);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}
}
