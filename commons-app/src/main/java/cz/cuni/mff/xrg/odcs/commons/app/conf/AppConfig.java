package cz.cuni.mff.xrg.odcs.commons.app.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.Resource;

/**
 * Class with global application configuration.
 * 
 * @author Petyr
 * @author Jan Vojt
 */
public class AppConfig {
	
	/**
	 * Path to configuration file.
	 * Not final, so that it can be overridden by run arguments (in backend).
	 */
	public static String confPath = System.getProperty("user.home") + "/.odcs/config.properties";

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
		LOG.log(Level.INFO, "Loading configuration from: {0}", confPath);
		try {
			loadFromStream(new FileInputStream(confPath));
		} catch (FileNotFoundException ex) {
			throw new ConfigFileNotFoundException(ex);
		}
	}
	
	/**
	 * Constructor building from Spring resource.
	 * 
	 * @param resource configuration
	 */
	public AppConfig(Resource resource) {
		LOG.log(Level.INFO, "Loading configuration from classpath resource.");
		try {
			loadFromStream(resource.getInputStream());
		} catch (IOException ex) {
			throw new ConfigFileNotFoundException(ex);
		}
	}
	
	/**
	 * Loads configuration from input stream.
	 * 
	 * @param stream 
	 */
	private void loadFromStream(InputStream stream) {
		try {
			prop.load(stream);
		} catch (IOException ex) {
			throw new ConfigFileNotFoundException(ex);
		} catch (IllegalArgumentException ex) {
			throw new MalformedConfigFileException(ex);
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
	
	/**
	 * @return defensive copy of wrapped properties.
	 */
	public Properties getProperties() {
		return new Properties(prop);
	}
}
