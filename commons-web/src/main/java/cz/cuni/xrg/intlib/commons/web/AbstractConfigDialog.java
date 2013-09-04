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

	/**
	 * Return text that should be used as a DPU tool tip. The text should
	 * contains information about configuration. If the configuration is not
	 * valid, or this functionality is not supported can return null.
	 * 
	 * @return Can be null.
	 */	
	public abstract String getToolTip();
	
	/**
	 * Return configuration summary that can be used as DPU description. The
	 * summary should be short and as much informative as possible. Return null
	 * in case of invalid configuration or it this functionality is not
	 * supported
	 * 
	 * @return Can be null.
	 */	
	public abstract String getDescription();
	
}
