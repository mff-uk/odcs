package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import java.util.List;

/**
 * Interface providing access to {@link User} data objects.
 * 
 * @author Jan Vojt
 */
public interface DbUser extends DbAccess<User> {

    /**
     * @return list of all users persisted in database
     */
    public List<User> getAll();

    /**
     * Find User by his unique username.
     * 
     * @param username
     * @return user
     */
    public User getByUsername(String username);

}
