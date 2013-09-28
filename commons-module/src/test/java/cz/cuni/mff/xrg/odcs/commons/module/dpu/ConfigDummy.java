package cz.cuni.mff.xrg.odcs.commons.module.dpu;

import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;

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
