package cz.cuni.mff.xrg.odcs.commons.module.dpu;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * Dummy configuration object for tests purpose.
 * @author Petyr
 *
 */
public class ConfigDummy extends DPUConfigObjectBase {
	
	public ConfigDummy() { }
	
	@Override
	public boolean isValid() {
		return true;
	}		
}
