package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.module.config.ConfigWrap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Convenience base class for configurable DPUs. Every DPU may either extend this class or 
 * directly implement {@link Configurable} interface.
 * 
 * @author Petyr
 * @author Tomas Knap
 */
public abstract class ConfigurableBase<T extends DPUConfigObject> implements Configurable<T>{

	/**
	 * Object configuration.
	 */
	protected T config;
	
	/**
	 * Container for configuration de/serialization.
	 */
	private ConfigWrap<T> configWrap;
	    
    public ConfigurableBase(T config) {
    	this.config = config;
    }
        
    @Override
    public void configure(byte[] c) throws ConfigException {
    	// set configuration for configWrap
    	configWrap.configure(c);
    	if (configWrap.getConf().isValid()) {
    		// use configuration from configWrap
    		config = configWrap.getConf();
    	} else {
    		throw new ConfigException("Invalid configuration.");
    	}
    }
    
    @Override
    public byte[] getConf() throws ConfigException {
    	// set configuration for wrap
    	configWrap.configure(config);
    	// return serialized version
    	return configWrap.getConfAsByte();
    }
    
}
