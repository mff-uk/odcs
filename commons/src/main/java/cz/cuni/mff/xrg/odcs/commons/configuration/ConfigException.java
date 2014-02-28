package cz.cuni.mff.xrg.odcs.commons.configuration;

/**
 * Exception used in relation do DPU invalid configuration.
 *
 * @author Petyr
 *
 */
public class ConfigException extends Exception {
	
	
	/**
	 * Create exception with default message. 
	 */
    public ConfigException() {
    	super("Invalid configuration.");
    }

	/**
	 * 
	 * @param cause Cause of the {@link ConfigException}.
	 */
    public ConfigException(Throwable cause) {
        super(cause);
    }

	/**
	 * 
	 * @param message Cause of the {@link ConfigException}.
	 */
    public ConfigException(String message) {
        super(message);
    }

	/**
	 * 
	 * @param message Description of action that throws.
	 * @param cause Cause of the {@link ConfigException}.
	 */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
