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
    
	@Override
	public void setConfig(byte[] conf) throws ConfigException {
		configWrap.configure(conf);
		if (configWrap.getConf().isValid()) {
			setConfiguration(configWrap.getConf());
		} else {
			// invalid configuration
			throw new ConfigException("Invalid configuration.");
		}
	}
	
	@Override
	public byte[] getConfig() throws ConfigException {
		configWrap.configure(getConfiguration());
		return configWrap.getConfAsByte();
	}
	
}
