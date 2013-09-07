package cz.cuni.xrg.intlib.commons.configuration;

/**
 * Represents an error caused by missing property in configuration.
 * 
 * @author Jan Vojt
 */
public class MissingConfigPropertyException extends ConfigPropertyException {

	public MissingConfigPropertyException(ConfigProperty property) {
		super(property);
	}

	/**
	 * Error message.
	 * 
	 * @return 
	 */
	@Override
	public String getMessage() {
		return "Config is missing property: " + property + ".";
	}
}
