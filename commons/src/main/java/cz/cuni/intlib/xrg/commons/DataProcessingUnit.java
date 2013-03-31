package cz.cuni.intlib.xrg.commons;

import java.util.Map;

import cz.cuni.intlib.xrg.commons.configuration.ExceptionSetting;

/**
 * Base DPU interface. The interface should not be implemented directly. 
 * To implement DPU use more specialised interfaces.
 * @author Petyr
 */
public interface DataProcessingUnit {
	
	/**
	 * Return DPU' name. The name can be shown to the user.
	 * @return DPU' name
	 */
	public String getName();
	
	/**
	 * Return DPU' description. The description can be 
	 * shown to the user as plain text.
	 * @return DPU' description
	 */
	public String getDescription();
	
	/**
	 * Return DPU type. The type must correspond with implemented sub-type.
	 * @return DPU' type
	 */
	public Type getType();
	
	/**
	 * Return current DPU settings. If the configuration dialog for 
	 * DPU is open then settings from dialog must be saved as current setting first.
	 * Function can throw if the setting dialog is open and the setting in the dialog
	 * is invalid.
	 * @return Serialised module settings.
	 * @throws ExceptionSetting
	 */
	public Map<String, Object> getSettings() throws ExceptionSetting;	
	
	/**
	 * Set DPU's settings. DPU settings.
	 * @param configuration Configuration to load.
	 * @throws ExceptionSetting Thrown in case of invalid setting.
	 */
	public void setSettings(Map<String, Object> configuration);
}
