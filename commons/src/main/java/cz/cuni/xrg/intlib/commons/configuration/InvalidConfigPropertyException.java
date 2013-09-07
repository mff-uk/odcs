package cz.cuni.xrg.intlib.commons.configuration;

/**
 * Represents error caused by invalid configuration value for given property.
 *
 * @author Jan Vojt
 */
public class InvalidConfigPropertyException extends ConfigPropertyException {
	
	/**
	 * Invalid value found in configuration.
	 */
	private String value;

	public InvalidConfigPropertyException(ConfigProperty property) {
		this(property, "unknown");
	}

	public InvalidConfigPropertyException(ConfigProperty property, String value) {
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
		return "Config property '" + property + "' has invalid value of '" + value + "'.";
	}
	
}
