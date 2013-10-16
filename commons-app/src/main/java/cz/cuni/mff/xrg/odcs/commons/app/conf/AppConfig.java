package cz.cuni.mff.xrg.odcs.commons.app.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
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
	 * Use factory methods for constructing configurations.
	 */
	private AppConfig() {
	}

	/**
	 * Factory building configuration from <code>Properties</code>.
	 * 
	 * @param properties
	 * @return configuration
	 */
	public static AppConfig loadFrom(Properties properties) {
		LOG.log(Level.INFO, "Loading configuration from Properties.");
		AppConfig config = new AppConfig();
		config.prop = properties;
		return config;
	}
	
	/**
	 * Constructor reads configuration file.
	 */
	public static AppConfig loadFromHome() {
		LOG.log(Level.INFO, "Loading configuration from: {0}", confPath);
		try {
			return loadFrom(new FileInputStream(confPath));
		} catch (FileNotFoundException ex) {
			throw new ConfigFileNotFoundException(ex);
		}
	}
	
	/**
	 * Constructor building from Spring resource.
	 * 
	 * @param resource configuration
	 */
	public static AppConfig loadFrom(Resource resource) {
		LOG.log(Level.INFO, "Loading configuration from classpath resource.");
		try {
			return loadFrom(resource.getInputStream());
		} catch (IOException ex) {
			throw new ConfigFileNotFoundException(ex);
		}
	}
	
	/**
	 * Loads configuration from input stream.
	 * 
	 * @param stream 
	 */
	public static AppConfig loadFrom(InputStream stream) {
		AppConfig config = new AppConfig();
		try {
			config.prop.load(stream);
		} catch (IOException ex) {
			throw new ConfigFileNotFoundException(ex);
		} catch (IllegalArgumentException ex) {
			throw new MalformedConfigFileException(ex);
		}
		
		return config;
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
	 * Creates a new configuration containing only a subset of this
	 * configuration properties matching given namespace. The keys of newly
	 * created configuration are trimmed off namespace prefix.
	 * 
	 * @param namespace
	 * @return new configuration
	 */
	public AppConfig getSubConfiguration(ConfigProperty namespace) {
		
		AppConfig subConfig = new AppConfig();
		String strNamespace = namespace.toString().concat(".");
		
		for (Map.Entry<Object, Object> e : prop.entrySet()) {
			if (e.getKey().toString().startsWith(strNamespace)) {
				String newNamespace = e.getKey().toString()
										.substring(strNamespace.length());
				subConfig.prop.setProperty(newNamespace, e.getValue().toString());
			}
		}
		
		return subConfig;
	}
	
	/**
	 * @return defensive copy of wrapped properties.
	 */
	public Properties getProperties() {
		return (Properties) prop.clone();
	}
}
