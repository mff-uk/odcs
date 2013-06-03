package cz.cuni.xrg.intlib.commons.web;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;

/**
 * Base abstract class for configuration dialog.
 * 
 * @author Petyr
 *
 */
public abstract class AbstractConfigurationDialog <C extends Configuration> extends CustomComponent {

	/**
	 * Set dialog interface according to passed configuration. If
	 * the passed configuration is invalid ConfigurationException can be thrown.
	 * @param conf Configuration object.
	 * @throws ConfigurationException
	 */
	public abstract void setConfiguration(C conf) throws ConfigurationException;
	
	/**
	 * Get configuration from dialog. In case of presence invalid configuration in 
	 * dialog throw ConfigurationException.
	 * @return Configuration object.
	 */
	public abstract C getConfiguration() throws ConfigurationException;
	
}
