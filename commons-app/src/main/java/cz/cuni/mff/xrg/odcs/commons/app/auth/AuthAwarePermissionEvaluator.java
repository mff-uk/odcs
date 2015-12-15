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
package cz.cuni.mff.xrg.odcs.commons.app.auth;

import org.springframework.security.access.PermissionEvaluator;

/**
 * Permission evaluator aware of current authentication context.
 * 
 * @see #hasPermission(java.lang.Object, java.lang.Object)
 * @author Jan Vojt
 */
public interface AuthAwarePermissionEvaluator extends PermissionEvaluator {

    /**
     * Resolves permissions on given target object for currently authenticated
     * user.
     * 
     * @param target
     *            entity to try permission on
     * @param perm
     *            permission requested on target
     * @return true if authenticated user has a given permission on target,
     *         false otherwise
     */
    public boolean hasPermission(Object target, Object perm);

}
