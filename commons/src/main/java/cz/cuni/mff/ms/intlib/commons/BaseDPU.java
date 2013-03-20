package cz.cuni.mff.ms.intlib.commons;

import com.vaadin.ui.CustomComponent;

/**
 * Base DPU interface.
 * @author Petyr
 *
 */
public interface BaseDPU {

	/**
	 * Return current DPU settings. If the configuration dialog for 
	 * DPU is open then settings from dialog must be saved as current setting first.
	 * Function can throw if the setting dialog is open and the setting in the dialog
	 * is invalid.
	 * 
	 * @return Serialised module settings.
	 * @throws ExceptionSetting
	 */
	public String getSettings() throws ExceptionSetting;	
	
	/**
	 * Set DPU's settings. DPU settings.
	 * @param value
	 * @throws ExceptionSetting Thrown in case of invalid setting.
	 */
	public void setSettings(String value);	
	
	/**
	 * Return configuration component for DPU. The configuration
	 * component must not contains close button. The component will be closed
	 * by application. 
	 * @return DPU's configuration component.
	 */
	public CustomComponent getConfigurationComponent();
	
	
}
