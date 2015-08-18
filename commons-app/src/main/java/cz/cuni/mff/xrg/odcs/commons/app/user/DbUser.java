/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.commons.app.user;

import java.util.List;
import java.util.Set;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

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
    
    /**
     * Find User by his unique external identifier.
     * 
     * @param extid
     * @return user
     */
    public User getByExtId(String extid);
    
}
