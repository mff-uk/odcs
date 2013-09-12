package cz.cuni.xrg.intlib.commons.module.config;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Base class for DPU's configuration. Provide default implementation 
 * of {@link #isValid()} method.
 * 
 * @author Petyr
 *
 */
public class DPUConfigObjectBase implements DPUConfigObject {

	@Override
	public boolean isValid() {
		return true;
	}

}
