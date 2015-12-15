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
package eu.unifiedviews.dataunit.relational.impl;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;

/**
 * Holds basic information about the single database table
 */
public class RelationalDataUnitEntryImpl implements RelationalDataUnit.Entry {

    private final String symbolicName;
    
    private final String tableName;
    
    public RelationalDataUnitEntryImpl(String symbolicName, String tableName) {
        this.symbolicName = symbolicName;
        this.tableName = tableName;
    }
    
    @Override
    public String getSymbolicName() throws DataUnitException {
        return this.symbolicName;
    }

    @Override
    public String getTableName() throws DataUnitException {
        return this.tableName;
    }

}
