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
package cz.cuni.mff.xrg.odcs.commons.app.dao;

/**
 * Types of filters that can be used in {@link DataQueryBuilder}.
 * 
 * @author Petyr
 * @deprecated unused class, will be removed
 */
@Deprecated
public enum FilterType {
    LIKE("LIKE"),
    EQUAL("="),
    NOT("!="),
    GREATER(">"),
    LESS("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<=");

    private final String sql;

    FilterType(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }

}
