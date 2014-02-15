
package cz.cuni.mff.xrg.odcs.commons.app.dao.db.datasource;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Customized <code>DataSource</code> for ODCS application configurable with
 * {@link AppConfig} and with prefilled Virtuoso JDBC Driver.
 * 
 * @author Jan Vojt
 */
public class VirtuosoDataSource extends BasicDataSource {
	
	/**
	 * Class name to be used as JDBC driver.
	 */
	public static final String DRIVER_CLASS_NAME = "virtuoso.jdbc4.Driver";
	
	/**
	 * JDBC connection string.
	 */
	private static final String JDBC_URL = "jdbc:virtuoso://%s:%s/charset=%s";

	/**
	 * <code>DataSource</code> constructed from configuration.
	 * 
	 * @param config application configuration
	 */
	public VirtuosoDataSource(AppConfig config) {
		setUrl(buildUrl(config));
		setUsername(config.getString(ConfigProperty.DATABASE_USER));
		setPassword(config.getString(ConfigProperty.DATABASE_PASSWORD));
		setDriverClassName(DRIVER_CLASS_NAME);
		
		// Auto-commit needs to be enabled for Virtuoso, see GH-953.
		// This prevents rollback when returning connection to the pool.
		// See PoolableConnectionFactory#passivateObject().
		setDefaultAutoCommit(true);
	}
	
	/**
	 * Connection URL factory.
	 * 
	 * @param config
	 * @return connection URL
	 */
	private static String buildUrl(AppConfig config) {
		return String.format(JDBC_URL,
				config.getString(ConfigProperty.DATABASE_HOSTNAME),
				config.getString(ConfigProperty.DATABASE_PORT),
				config.getString(ConfigProperty.DATABASE_CHARSET)
		);
	}

}
