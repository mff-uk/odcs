package cz.cuni.xrg.intlib.commons.app.conf;

/**
 * Represents error caused by invalid configuration value for given property.
 *
 * @author Jan Vojt
 */
public class InvalidConfigurationPropertyException extends ConfigurationPropertyException {
	
	/**
	 * Invalid value found in configuration.
	 */
	private String value;

	public InvalidConfigurationPropertyException(ConfProperty property) {
		this(property, "unknown");
	}

	public InvalidConfigurationPropertyException(ConfProperty property, String value) {
		super(property);
		this.value = value;
	}

	/**
	 * Error message.
	 * 
	 * @return 
	 */
	@Override
	public String getMessage() {
		return "Configuration property '" + property + "' has invalid value of '" + value + "'.";
	}
	
}
