package cz.cuni.xrg.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Dummy configuration object for tests purpose.
 * @author Petyr
 *
 */
public class ConfigDummy implements DPUConfigObject {
	
	public ConfigDummy() { }
	
	@Override
	public boolean isValid() {
		return true;
	}		
}
