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
package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

/**
 * Partial implementation of {@link DbAccess} interface.
 * 
 * @author Petyr
 * @param <T>
 */
public abstract class DbAccessBase<T extends DataObject>
        extends DbAccessReadBase<T>
        implements DbAccess<T> {

    public DbAccessBase(Class<T> entityClass) {
        super(entityClass);
    }

    @Transactional
    @Override
    public void save(T object) {
        if (object.getId() == null) {
            em.persist(object);
        } else {
            em.merge(object);
        }
    }

    @Transactional
    @Override
    public void delete(T object) {
        // we might be trying to remove detached entity
        if (!em.contains(object) && object.getId() != null) {
            object = getInstance(object.getId());
        }
        em.remove(object);
    }

}
