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
public abstract class AbstractConfigDialog<C extends DPUConfigObject>
		extends CustomComponent {

	/**
	 * Deserialize configuration and call
	 * {@link #setConfiguration(DPUConfigObject)}
	 * 
	 * @param conf Serialized configuration object.
	 * @throws ConfigException
	 */
	public abstract void setConfig(byte[] conf) throws ConfigException;

	/**
	 * Return serialized result of {@link #getConfiguration()}
	 * 
	 * @return Serialized configuration object.
	 */
	public abstract byte[] getConfig() throws ConfigException;

}
