package cz.cuni.xrg.intlib.commons.configuration;

/**
 * Exception used in relation do DPU invalid setting.
 *
 * @author Petyr
 *
 */
public class ConfigurationException extends RuntimeException {
private final String message = "Configuration is wrong set.";

    public ConfigurationException() {
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    


}
