
package virtuoso;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Customized <code>DataSource</code> for ODCS application configurable with
 * {@link AppConfig} and with prefilled Virtuoso JDBC Driver.
 *
 * <p><b>NOTE: This class is not an actual connection pool; it does not actually
 * pool Connections.</b> It just serves as simple replacement for a full-blown
 * connection pool, implementing the same standard interface, but creating new
 * Connections on every call.
 * 
 * <p> TODO replace with connection pool
 * 
 * @author Jan Vojt
 */
public class ConfigurableDataSource extends DriverManagerDataSource {
	
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
		super(
			buildUrl(config),
			config.getString(ConfigProperty.VIRTUOSO_USER),
			config.getString(ConfigProperty.VIRTUOSO_PASSWORD)
		);
		setDriverClassName(DRIVER_CLASS_NAME);
	}
	
	/**
	 * Connection URL factory.
	 * 
	 * @param config
	 * @return 
	 */
	private static String buildUrl(AppConfig config) {
		String url = "jdbc:virtuoso://%s:%s/charset=%s";
		String host = config.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
		String port = config.getString(ConfigProperty.VIRTUOSO_PORT);
		String charset = config.getString(ConfigProperty.VIRTUOSO_CHARSET);
		return String.format(url, host, port, charset);
	}

}
