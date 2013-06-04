package gui;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.module.gui.AbstractConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU configuration.
 * 
 * @author Petyr
 *
 */
public class ConfigDialog extends AbstractConfigDialog {

	@Override
    public void getConfiguration(Config config) throws ConfigException {
    	// TODO: save current dialog configuration into config, in case of invalid configuration throw ConfigException
    }

	@Override
    public void setConfiguration(Config conf) throws ConfigException {
        // TODO: load configuration from config into dialog, in case of invalid configuration throw ConfigException
    }
	
}
