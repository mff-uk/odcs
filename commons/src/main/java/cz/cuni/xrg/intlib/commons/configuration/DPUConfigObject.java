package cz.cuni.xrg.intlib.commons.configuration;

import java.io.Serializable;

/**
 * Base interface for DPU's configuration. <b>All the configuration objects must
 * provide public parameter-less constructor!</b>
 * 
 * @author Petyr
 */
public interface DPUConfigObject extends Serializable {

	/**
	 * Check if configuration is valid ie. can be used to configure DPU or
	 * configuration dialog.
	 * 
	 * @return
	 */
	boolean isValid();
	 
}
