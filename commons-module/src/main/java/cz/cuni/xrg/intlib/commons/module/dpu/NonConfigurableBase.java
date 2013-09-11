package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.dpu.DPU;

/**
 * Base class for DPUs without configuration and configuration dialog. Use this
 * for simple-testing DPU's.
 *  
 * @author Petyr
 *
 */
public abstract class NonConfigurableBase implements DPU {
	
	@Override
	public void cleanUp() {	}

}
