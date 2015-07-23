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
package eu.unifiedviews.dataunit.relational.db;

/**
 * Database wrapper configuration interface
 * Provides all configuration parameters needed to create a database
 * and provide database connections
 * 
 * @author Tomas
 */
public interface DatabaseWrapperConfigIF {

    /**
     * Get database name used to create the internal database
     * 
     * @return database name
     */
    String getDatabaseURL();

    /**
     * User name to connect to the database
     * 
     * @return user name
     */
    String getUserName();

    /**
     * User password for the database
     * 
     * @return password
     */
    String getPassword();

    /**
     * Get max. pooled connections to the database
     * 
     * @return Limit of pooled database connections
     */
    int getMaxPooledConnections();

    /**
     * Return name of JDBC driver which can be used to connect to the underlying relational database
     * 
     * @return Name of JDBC driver used to connect to database
     */
    String getJDBCDriverName();

}
