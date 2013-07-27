package cz.cuni.xrg.intlib.commons.web;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;

/**
 * Base abstract class for configuration dialog.
 * 
 * @author Petyr
 *
 */
public abstract class AbstractConfigDialog <C extends DPUConfigObject> extends CustomComponent {

	/**
	 * Set dialog interface according to passed configuration. If
	 * the passed configuration is invalid ConfigException can be thrown.
	 * @param conf Config object.
	 * @throws ConfigException
	 */
	public abstract void setConfiguration(C conf) throws ConfigException;
	
	/**
	 * Get configuration from dialog. In case of presence invalid configuration in 
	 * dialog throw ConfigException.
	 * @return Config object.
	 */
	public abstract C getConfiguration() throws ConfigException;
	
}
