
package virtuoso;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Customized <code>DataSource</code> for ODCS application configurable with
 * {@link AppConfig} and with prefilled Virtuoso JDBC Driver.
 * 
 * @author Jan Vojt
 */
public class ConfigurableDataSource extends BasicDataSource {
	
	/**
	 * Class name to be used as JDBC driver.
	 */
	public static final String DRIVER_CLASS_NAME = "virtuoso.jdbc4.Driver";

	/**
	 * <code>DataSource</code> constructed from configuration.
	 * 
	 * @param config application configuration
	 */
	public ConfigurableDataSource(AppConfig config) {
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
	 * @return 
	 */
	private static String buildUrl(AppConfig config) {
		String url = "jdbc:virtuoso://%s:%s/charset=%s";
		String host = config.getString(ConfigProperty.DATABASE_HOSTNAME);
		String port = config.getString(ConfigProperty.DATABASE_PORT);
		String charset = config.getString(ConfigProperty.DATABASE_CHARSET);
		return String.format(url, host, port, charset);
	}

}
