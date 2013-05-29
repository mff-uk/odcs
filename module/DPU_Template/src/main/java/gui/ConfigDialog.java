package gui;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;

public class ConfigDialog extends CustomComponent {

    /**
     * Return current configuration from dialog. In case of invalid 
     * configuration should throw.
     *
     * @throws ConfigurationException
     * @return current configuration or null
     */
    public void getConfiguration(Configuration config) throws ConfigurationException {
    	// TODO: save current dialog configuration into config, in case of invalid configuration throw ConfigurationException
    }

    /**
     * Load values from configuration into dialog.
     *
     * @throws ConfigurationException
     * @param conf
     */
    public void setConfiguration(Configuration conf) throws ConfigurationException {
        // TODO: load configuration from config into dialog, in case of invalid configuration throw ConfigurationException
    }
	
}
