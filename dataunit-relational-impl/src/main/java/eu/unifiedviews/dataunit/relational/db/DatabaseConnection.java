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
package eu.unifiedviews.dataunit.relational.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link DatabaseConnection} interface for providing connection to the underlying database
 */
public interface DatabaseConnection {

    /**
     * Return connection to the underlying database
     * Connection is returned opened and it is responsibility of the caller to properly close it
     * 
     * @return Connection to the database
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Close all connections to the underlying database
     * 
     * @throws Exception
     */
    void close();
}
