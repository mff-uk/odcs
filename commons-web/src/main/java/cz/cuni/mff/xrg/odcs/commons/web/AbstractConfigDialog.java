package cz.cuni.mff.xrg.odcs.commons.web;

import com.vaadin.ui.CustomComponent;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;

/**
 * Base abstract class for a configuration dialog. DPU developer should extend 
 * his DPU's configuration dialog from {@link BaseConfigDialog} instead of this class. 
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
	 * <b>This functionality is not currently supported by ODCS, but the 
	 * returned value may be user in further.</b>
	 */	
	public abstract String getToolTip();
	
	/**
	 * Return configuration summary that can be used as DPU description. The
	 * summary should be short and as much informative as possible. Return null
	 * in case of invalid configuration or it this functionality is not
	 * supported. The returned string should be reasonably short.
	 * 
	 * @return Can be null.
	 */	
    @Override
	public abstract String getDescription();
	
    /**
     * Compare last configuration and current dialog's configuration. If any
     * exception is thrown then return false.
     * The last configuration is updated in calls {@link #getConfig()}
     * and {@link #setConfig(byte[])}.
     *  
     * @return False if configurations are valid and are different.
     */
    public abstract boolean hasConfigChanged();
    
}
