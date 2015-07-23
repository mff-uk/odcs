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
package cz.cuni.mff.xrg.odcs.backend.logback;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;

/**
 * Connection source for logback. Uses {@link BasicDataSource} as connection
 * pool manager. Database login is loaded from {@link AppConfig}.
 * 
 * @author Jan Vojt
 */
public class LoggingConnectionSource extends DriverManagerConnectionSource {

    /**
     * Database connection pool.
     */
    private final DataSource dataSource;

    public LoggingConnectionSource(DataSource source) {
        dataSource = source;
    }

    /**
     * Initializes connection connection source. Driver class name needs to be
     * set here to be able to detect connection properties.
     */
    @Override
    public void start() {
        if (dataSource instanceof BasicDataSource) {
            setDriverClass(((BasicDataSource) dataSource).getDriverClassName());
        }
        super.start();
    }

    /**
     * Gets a connection from the connection pool.
     * 
     * @return database connection
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
