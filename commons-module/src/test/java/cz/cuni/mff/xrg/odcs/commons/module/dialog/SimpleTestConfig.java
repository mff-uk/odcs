package cz.cuni.mff.xrg.odcs.commons.module.dialog;

import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;

/**
 * Simple configuration object used in {@Link BaseConfigDialogTest}.
 * 
 * @author Petyr
 *
 */
public class SimpleTestConfig implements DPUConfigObject {

	public String text;
	
	public int value;
	
	public SimpleTestConfig(String text, int value) {
		this.text = text;
		this.value = value;
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
}
