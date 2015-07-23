/*******************************************************************************
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
 *******************************************************************************/
/*******************************************************************************
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
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.user;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Interface providing access to {@link User} data objects.
 * 
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbUserImpl extends DbAccessBase<User> implements DbUser {

    public DbUserImpl() {
        super(User.class);
    }

    @Override
    public List<User> getAll() {
        final String stringQuery = "SELECT e FROM User e";
        return executeList(stringQuery);
    }

    @Override
    public User getByUsername(String username) {
        final String stringQuery = "SELECT e FROM User e WHERE e.username = :uname";
        TypedQuery<User> query = createTypedQuery(stringQuery);
        query.setParameter("uname", username);
        return execute(query);
    }

    @Override
    public User getByExtId(String extid) {
        final String stringQuery = "SELECT e FROM User e WHERE e.externalIdentifier = :extid";
        TypedQuery<User> query = createTypedQuery(stringQuery);
        query.setParameter("extid", extid);
        return execute(query);
    }
}
