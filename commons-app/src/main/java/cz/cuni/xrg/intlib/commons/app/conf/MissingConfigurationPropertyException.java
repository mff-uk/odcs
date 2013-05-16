package cz.cuni.xrg.intlib.commons.app.conf;

/**
 * Represents an error caused by missing property in configuration.
 * 
 * @author Jan Vojt
 */
public class MissingConfigurationPropertyException extends ConfigurationPropertyException {

	public MissingConfigurationPropertyException(ConfProperty property) {
		super(property);
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
