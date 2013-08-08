package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.module.config.ConfigWrap;

/**
 * Convenience base class for configurable DPUs. Every DPU may either extend
 * this class or directly implement {@link Configurable} interface.
 * 
 * @author Petyr
 * @author Tomas Knap
 */
public abstract class ConfigurableBase<C extends DPUConfigObject>
		implements Configurable<C> {

	/**
	 * Object configuration.
	 */
	protected C config;

	/**
	 * Container for configuration de/serialization.
	 */
	private ConfigWrap<C> configWrap;

	public ConfigurableBase(C config) {
		this.config = config;
		this.configWrap = new ConfigWrap<C>(config);
	}

	@Override
	public void configure(byte[] c) throws ConfigException {
		// set configuration for configWrap
		C newConfig = configWrap.deserialize(c);
		if (newConfig == null ) {
			return;
		}
		if (newConfig.isValid()) {
			// use configuration from configWrap
			config = newConfig;
		} else {
			throw new ConfigException("Invalid configuration.");
		}
	}

	@Override
	public byte[] getConf() throws ConfigException {
		return configWrap.serialize(config);
	}

}
