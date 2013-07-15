package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;

/**
 * Convenience base class for configurable DPUs. Every DPU may either extend this class or 
 * directly implement {@link Configurable} interface.
 * 
 * @author tomasknap
 */
public abstract class ConfigurableBase<T extends Config> implements Configurable<T>{

    /**
     * To store the configuration object holding configuration for the DPU.
     */
    protected T config ;
    
    @Override
    public void configure(T c) throws ConfigException {
        this.config = c; 
    }

    @Override
    public T getConfiguration() {
        return config;
    }
    
}
// TODO Petyr: Move to commons-module and test
