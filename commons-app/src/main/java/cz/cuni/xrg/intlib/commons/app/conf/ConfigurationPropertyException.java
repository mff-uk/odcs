package cz.cuni.xrg.intlib.commons.app.conf;

/**
 * Represents an error caused property in configuration.
 * 
 * @author Jan Vojt
 */
public abstract class ConfigurationPropertyException extends RuntimeException {
	
	/**
	 * Name of missing property.
	 */
	protected ConfProperty property;

	/**
	 * Constructs an instance of
	 * <code>MissingConfigurationPropertyException</code> with the specified
	 * property printed in message.
	 *
	 * @param property name
	 */
	public ConfigurationPropertyException(ConfProperty property) {
		this.property = property;
	}

	/**
	 * Error message.
	 * 
	 * @return 
	 */
	@Override
	public String getMessage() {
		return "Configuration is missing property: " + property + ".";
	}
	
}
