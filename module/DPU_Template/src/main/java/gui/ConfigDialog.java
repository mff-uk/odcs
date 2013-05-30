package gui;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.module.gui.AbstractConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU configuration.
 * 
 * @author Petyr
 *
 */
public class ConfigDialog extends AbstractConfigDialog {

	@Override
    public void getConfiguration(Configuration config) throws ConfigurationException {
    	// TODO: save current dialog configuration into config, in case of invalid configuration throw ConfigurationException
    }

	@Override
    public void setConfiguration(Configuration conf) throws ConfigurationException {
        // TODO: load configuration from config into dialog, in case of invalid configuration throw ConfigurationException
    }
	
}
