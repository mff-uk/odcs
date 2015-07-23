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
package eu.unifiedviews.dataunit.relational.repository;

import java.sql.SQLException;

import eu.unifiedviews.dataunit.relational.db.DataUnitDatabaseConnectionProvider;

/**
 * Interface for relational database repository used by relational data units
 */
public interface ManagableRelationalRepository {

    public static final String BASE_DATABASE_NAME = "dataUnitDb";

    /**
     * Type of relational repository
     */
    public static enum Type {
        FILE, IN_MEMORY
    }

    /**
     * Get SQL connection provider to the underlying database
     * 
     * @return Implementation of {@link DataUnitDatabaseConnectionProvider} which provides connections to the underlying database
     * @throws SQLException
     */
    DataUnitDatabaseConnectionProvider getDatabaseConnectionProvider();

    /**
     * Terminate all connections to the database and shutdown the database
     * 
     * @throws Exception
     */
    void release() throws Exception;

}
