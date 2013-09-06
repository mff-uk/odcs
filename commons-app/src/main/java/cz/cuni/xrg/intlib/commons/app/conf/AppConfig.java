package cz.cuni.xrg.intlib.commons.app.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Class with backend application configuration.
 * 
 * @author Petyr
 * @author Jan Vojt
 */
public class AppConfig {
	
	/**
	 * Path to configuration file.
	 * Not final, so that it can be overridden by run arguments (in backend).
	 */
	public static String confPath = System.getProperty("user.home") + "/.intlib/config.properties";

	/**
	 * Modifiable configuration itself.
	 */
	private Properties prop = new Properties();
	
	/**
	 * Logging gateway.
	 */
	private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());
	
	/**
	 * Constructor reads configuration file.
	 */
	public AppConfig() {
		LOG.log(Level.INFO, "Loading configuration from: {}", confPath);
		try {
			FileInputStream stream = new FileInputStream(confPath);
			prop.load(stream);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Could not read configuration file at " + confPath + ".", ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Gets value of given configuration property.
	 * 
	 * @param key
	 * @return 
	 */
	public String getString(ConfigProperty key) {
		String value = prop.getProperty(key.toString());
		if (value == null) {
			throw new MissingConfigPropertyException(key);
		}
		return value;
	}
	
	/**
	 * Gets integer value of given configuration property.
	 * 
	 * @param key
	 * @return 
	 */
	public int getInteger(ConfigProperty key) {
		String value = getString(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new InvalidConfigPropertyException(key, value);
		}
	}
	
	/**
	 * Gets boolean value of given configuration property.
	 * 
	 * @param key
	 * @return 
	 */
	public boolean getBoolean(ConfigProperty key) {
		return Boolean.parseBoolean(getString(key));
	}
}
