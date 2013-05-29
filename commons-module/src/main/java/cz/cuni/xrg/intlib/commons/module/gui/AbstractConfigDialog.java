package cz.cuni.xrg.intlib.commons.module.gui;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;

/**
 * Abstract class for configuration dialog.
 * 
 * @author Petyr
 *
 */
public abstract class AbstractConfigDialog extends CustomComponent {

	 /**
     * Return current configuration from dialog. If the configuration is
     * invalid throw ConfigurationException.
     *
     * @throws ConfigurationException
     * @return current configuration
     */
    public abstract void getConfiguration(Configuration config) throws ConfigurationException;
	
    /**
     * Load values from configuration into dialog. If the configuration is invalid 
     * throw ConfigurationException.
     *
     * @throws ConfigurationException
     * @param conf
     */
    public abstract void setConfiguration(Configuration conf) throws ConfigurationException;
    
}
