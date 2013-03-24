package module;

import com.vaadin.ui.CustomComponent;

public class DummyModule implements cz.cuni.mff.ms.intlib.commons.BaseDPU {

	/**
	 * Some value.
	 */
	protected String value = "unset";
	
	protected gui.ConfigDialog conDialog = null;
	
	public CustomComponent GetConfigurationDialog() {		
		if (this.conDialog == null) {
			this.conDialog = new gui.ConfigDialog(this.value);
		}
		return this.conDialog;
	}
	
	public String GetSettings() {
		if (this.conDialog == null) {
		} else {
			// update setting
			this.value = this.conDialog.getSetting();
		}
			
			// getSetting
		return value;
	}
	
	/**
	 * Setter for this.value;
	 * @param value
	 */
	public void SetSettings(String value) {
		this.value = value;
		if (this.conDialog != null) {
			this.conDialog.setSetting(this.value);
		}		
	}
}
