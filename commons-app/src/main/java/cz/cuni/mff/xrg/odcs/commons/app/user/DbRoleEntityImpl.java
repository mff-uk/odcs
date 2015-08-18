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

import javax.persistence.TypedQuery;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

public class DbRoleEntityImpl extends DbAccessBase<RoleEntity> implements
		DbRoleEntity {

	public DbRoleEntityImpl() {
		super(RoleEntity.class);
	}

	@Override
	public List<RoleEntity> getAllRoles() {
        final String stringQuery = "SELECT r FROM RoleEntity r";
        return executeList(stringQuery);
  	}

	@Override
	public RoleEntity getRoleByName(String name) {
        final String stringQuery = "SELECT e FROM RoleEntity e WHERE e.name = :rname";
        TypedQuery<RoleEntity> query = createTypedQuery(stringQuery);
        query.setParameter("rname", name);
        return execute(query);
	}
}