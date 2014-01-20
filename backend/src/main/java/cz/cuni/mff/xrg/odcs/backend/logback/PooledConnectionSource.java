package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.datasource.VirtuosoDataSource;

/**
 * Connection source for logback. Uses {@link BasicDataSource} as connection
 * pool manager. Database login is loaded from {@link AppConfig}.
 *
 * @author Jan Vojt
 */
public class PooledConnectionSource extends DriverManagerConnectionSource {
	
	private DataSource pool;
	
	/**
	 * Application configuration.
	 */
	private final AppConfig appConfig;
	
	public PooledConnectionSource(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	/**
	 * Setup connection pool.
	 */
	@Override
	public void start() {
		BasicDataSource dataSource = new VirtuosoDataSource(
				appConfig.getSubConfiguration(ConfigProperty.RDBMS)
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
