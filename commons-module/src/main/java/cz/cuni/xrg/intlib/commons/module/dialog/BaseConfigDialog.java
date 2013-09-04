package cz.cuni.xrg.intlib.commons.module.dialog;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.module.config.ConfigWrap;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;

public abstract class BaseConfigDialog<C extends DPUConfigObject>
		extends AbstractConfigDialog<C> {

	/**
	 * Used to convert configuration object into byte array and back.
	 */
    private ConfigWrap<C> configWrap; 
	    
    public BaseConfigDialog(Class<C> configClass) {
    	this.configWrap = new ConfigWrap<>(configClass);
    }    
    
	@Override
	public void setConfig(byte[] conf) throws ConfigException {
		C config = configWrap.deserialize(conf);
		if (config == null) {
			// null -> try to use default configuration
			config = configWrap.createInstance();
		}
		if (config != null && config.isValid()) {
			setConfiguration(config);
		} else {
			// invalid configuration
			throw new ConfigException("Invalid configuration.");
		}
	}
	
	@Override
	public byte[] getConfig() throws ConfigException {
		return configWrap.serialize(getConfiguration());
	}
	
	@Override
	public String getToolTip() {
		return null;
	}
	
	@Override
	public String getDescription() {
		return null;
	}
	
	/**
	 * Set dialog interface according to passed configuration. If
	 * the passed configuration is invalid ConfigException can be thrown.
	 * @param conf Configuration object.
	 * @throws ConfigException
	 */
	protected abstract void setConfiguration(C conf) throws ConfigException;
	
	/**
	 * Get configuration from dialog. In case of presence invalid configuration in 
	 * dialog throw ConfigException.
	 * @return getConfiguration object.
	 */
	protected abstract C getConfiguration() throws ConfigException;	
}
