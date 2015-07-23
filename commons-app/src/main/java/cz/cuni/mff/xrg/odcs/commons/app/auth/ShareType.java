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
package cz.cuni.mff.xrg.odcs.commons.app.auth;

import java.util.EnumSet;

/**
 * Types of Entity shares, which define how is authorization decided
 * for {@link cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject} implementing {@link SharedEntity}.
 * 
 * @see SharedEntity
 * @see AuthAwarePermissionEvaluator
 * @see cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAuthorizator
 * @author Jan Vojt
 */
public enum ShareType {

    /**
     * Entity is private, no other user except admin and owner can see it.
     */
    PRIVATE,

    /**
     * Entity is publicly viewable, but only admin and owner can modify it.
     */
    PUBLIC_RO,

    /**
     * Entity is public, anyone can make changes to it. Only admin and owner
     * can delete it.
     */
    PUBLIC_RW;

    /**
     * Set of states in which entity is publicly viewable.
     */
    public static final EnumSet<ShareType> PUBLIC = EnumSet.of(
            PUBLIC_RO,
            PUBLIC_RW
            );
}
