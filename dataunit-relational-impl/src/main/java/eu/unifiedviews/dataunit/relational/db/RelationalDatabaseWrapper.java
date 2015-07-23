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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * {@link RelationalDatabaseWrapper} is a wrapper around a relational database
 * It provides access to this database by creating a new in memory database based on {@link DatabaseWrapperConfigIF} configuration.
 * Database wrapper uses {@link PooledDatabaseConnectionImpl}, which uses pool of database connections.
 */
public class RelationalDatabaseWrapper implements DatabaseWrapperIF {

    private final DatabaseConnection connectionProvider;

    private final DatabaseWrapperConfigIF config;

    private boolean bActive = true;

    /**
     * Creates new wrapper around the relational database defined by {@link DatabaseWrapperConfigIF}
     * 
     * @param config
     *            Connection parameters for the database
     * @throws Exception
     */
    public RelationalDatabaseWrapper(DatabaseWrapperConfigIF config) throws Exception {
        this.connectionProvider = new PooledDatabaseConnectionImpl(config);
        this.config = config;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connectionProvider.getConnection();
    }

    @Override
    public DatabaseWrapperConfigIF getConfiguration() {
        return this.config;
    }

    @Override
    public void shutdown() {
        this.connectionProvider.close();
        this.bActive = false;
    }

    @Override
    public boolean isActive() {
        return this.bActive;
    }

    @Override
    public Connection getConnectionForUser(String userName, String password) throws SQLException {
        Connection conn = null;
        try {
            Class.forName(this.config.getJDBCDriverName());
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver class not found");
        }

        Properties dbProperties = new Properties();
        dbProperties.put("user", userName);
        dbProperties.put("password", password);
        conn = DriverManager.getConnection(this.config.getDatabaseURL(), dbProperties);

        return conn;
    }

}
