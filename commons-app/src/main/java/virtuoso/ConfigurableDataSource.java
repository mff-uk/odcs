
package virtuoso;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Customized <code>DataSource</code> for Intlib application configurable with
 * {@link AppConfig} and with prefilled Virtuoso JDBC Driver.
 *
 * @author Jan Vojt
 */
public class ConfigurableDataSource extends DriverManagerDataSource {
	
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
		String url = "jdbc:virtuoso://%s:%s/";
		String host = config.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
		String port = config.getString(ConfigProperty.VIRTUOSO_PORT);
		return String.format(url, host, port);
	}

}
