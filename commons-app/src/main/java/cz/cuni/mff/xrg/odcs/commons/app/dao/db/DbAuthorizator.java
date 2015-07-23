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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Adds filters to container based on authorization logic.
 * 
 * @author Jan Vojt
 * @author Petyr
 */
public interface DbAuthorizator {

    /**
     * Return authorization {@link Predicate}.
     * 
     * @param cb
     * @param root
     * @param entityClass
     * @return authorization predicate for DB query
     */
    Predicate getAuthorizationPredicate(CriteriaBuilder cb, Path<?> root, Class<?> entityClass);

}
