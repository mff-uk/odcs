package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;

/**
 * Indicate that {@link ConfigurableBase} failed to create instance of
 * configuration.
 * 
 * @author Petyr
 *
 */
public class FailedToCreateConfigException extends ConfigException {

    public FailedToCreateConfigException(Throwable cause) {
        super(cause);
    }	
	
}
