package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import java.util.List;

/**
 * Facade for managing persisted User entities.
 * 
 * @author Jan Vojt
 */
public interface UserFacade extends Facade {

    /**
     * Factory for a new User.
     * 
     * @param username
     * @param password
     * @param email
     * @return new user instance
     */
    User createUser(String username, String password, EmailAddress email);

    /**
     * @return list of all users persisted in database
     */
    List<User> getAllUsers();

    /**
     * @param id
     *            primary key
     * @return user with given id or <code>null<code>
     */
    User getUser(long id);

    /**
     * Find User by his unique username. This method is not secured, so that
     * yet unauthenticated users can login.
     * 
     * @param username
     * @return user
     */
    User getUserByUsername(String username);

    /**
     * Saves any modifications made to the User into the database.
     * 
     * @param user
     */
    void save(User user);

    /**
     * Deletes pipeline from database.
     * 
     * @param user
     */
    void delete(User user);

}
