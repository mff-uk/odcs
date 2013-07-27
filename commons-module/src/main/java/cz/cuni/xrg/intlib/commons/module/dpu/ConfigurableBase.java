package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Convenience base class for configurable DPUs. Every DPU may either extend this class or 
 * directly implement {@link Configurable} interface.
 * 
 * @author tomasknap
 * @author Petyr
 */
public abstract class ConfigurableBase<T extends DPUConfigObject> implements Configurable<T>{

    /**
     * To store the configuration object holding configuration for the DPU.
     */
    protected T config ;
    
    public ConfigurableBase() throws FailedToCreateConfigException {    	
    	try {
			this.config = (T)getConfigClass().newInstance();
		} catch (InstantiationException|IllegalAccessException e) {
			throw new FailedToCreateConfigException(e);
		} 
    }
    
    @SuppressWarnings ("unchecked")
    private Class<T> getConfigClass() {
    	Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];    	
    }
    
    @Override
    public void configure(T c) throws ConfigException {
        this.config = c; 
    }

    @Override
    public T getConfiguration() {
        return config;
    }
    
}
