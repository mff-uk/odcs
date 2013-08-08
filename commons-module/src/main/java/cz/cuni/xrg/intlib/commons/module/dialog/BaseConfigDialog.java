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
	
    public BaseConfigDialog(C config) {
    	this.configWrap = new ConfigWrap<C>(config);
    }
    
    public BaseConfigDialog(Class<C> configClass) {
    	this.configWrap = new ConfigWrap<C>(configClass);
    }    
    
	@Override
	public void setConfig(byte[] conf) throws ConfigException {
		C config = configWrap.deserialize(conf);		
		if (config.isValid()) {
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
	
}
