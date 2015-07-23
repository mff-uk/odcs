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
package eu.unifiedviews.dataunit.relational.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database repository interface
 * Defines the basic methods required by database repository wrapper
 * 
 * @author Tomas
 */
public interface DatabaseWrapperIF {
    /**
     * Get connection to the underlying database
     * 
     * @return Connection to the underlying database
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Get connection to the underlying database for given user
     * 
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    Connection getConnectionForUser(String userName, String password) throws SQLException;

    /**
     * Get configuration of the database wrapper
     * 
     * @return Database wrapper configuration
     */
    DatabaseWrapperConfigIF getConfiguration();

    /**
     * Shuts down the connections
     */
    void shutdown();

    /**
     * Whether the connection wrapper is active
     * 
     * @return
     */
    boolean isActive();

}
