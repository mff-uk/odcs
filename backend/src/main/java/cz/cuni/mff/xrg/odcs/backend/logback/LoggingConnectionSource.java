package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

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
	private DataSource dataSource;
	
	public LoggingConnectionSource(DataSource source) {
		dataSource = source;
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
