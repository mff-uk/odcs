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
 * {@link DefaultDatabaseConfig} is configuration object for internal database wrapper
 * It provides communication parameters for database (db name, user name, password)
 * and optional connections pool parameters
 * By default, connections pool is used and max. connections pooled is set to 20
 */
public class DefaultDatabaseConfig implements DatabaseWrapperConfigIF {

    private final static int DEFAULT_MAX_POOLED_CONNECTIONS = 20;

    private String databaseURL;

    private String userName;

    private String password;

    private String jdbcDriverName;

    private int maxPooledConnections = DEFAULT_MAX_POOLED_CONNECTIONS;

    public DefaultDatabaseConfig(String databaseURL, String userName, String password, String jdbcDriverName) {
        this.databaseURL = databaseURL;
        this.userName = userName;
        this.password = password;
        this.jdbcDriverName = jdbcDriverName;
    }

    @Override
    public String getDatabaseURL() {
        return this.databaseURL;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setNumberOfPooledConnections(int maxPooledConnections) {
        this.maxPooledConnections = maxPooledConnections;
    }

    @Override
    public int getMaxPooledConnections() {
        return this.maxPooledConnections;
    }

    @Override
    public String getJDBCDriverName() {
        return this.jdbcDriverName;
    }

}
