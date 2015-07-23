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

import org.springframework.security.core.GrantedAuthority;

/**
 * Set of roles in the system.
 * 
 * @author Jiri Tomes
 */
@Deprecated
public enum Role implements GrantedAuthority {

    ROLE_USER("User"),
    ROLE_ADMIN("Administrator");

    /**
     * Human-readable string representation of role.
     */
    private final String role;

    private Role(String role) {
        this.role = role;
    }

    /**
     * Returns string value of authority.
     * 
     * @return String value of authority.
     */
    @Override
    public String getAuthority() {
        return name();
    }

    /**
     * Returns string value of role.
     * 
     * @return string value of role.
     */
    @Override
    public String toString() {
        return role;
    }
}
