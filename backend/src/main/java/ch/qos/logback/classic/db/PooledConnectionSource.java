package ch.qos.logback.classic.db;

import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import virtuoso.ConfigurableDataSource;

/**
 * Connection source for logback. Uses {@link BasicDataSource} as connection
 * pool manager. Database login is loaded from {@link AppConfig}.
 *
 * @author Jan Vojt
 */
public class PooledConnectionSource extends DriverManagerConnectionSource {
	
	private DataSource pool;
	
	/**
	 * Setup connection pool.
	 */
	@Override
	public void start() {
		// This is ugly, but Spring's autowiring is not available yet. Spring
		// initializes logging (and thus logback) before application context is
		// initialized. This could be circumvented by setting up a listener for
		// Spring context initialization and replace logging appender afterwards.
		// However, this is too complicated, so we use this simple but ugly
		// manual initialization of dataSource instead. :(
		AppConfig appConfig = AppConfig.loadFromHome();
		BasicDataSource dataSource = new ConfigurableDataSource(
				appConfig.getSubConfiguration(ConfigProperty.VIRTUOSO_RDBMS)
		);
		setDriverClass(dataSource.getDriverClassName());
		pool = dataSource;
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
		return pool.getConnection();
	}
}
