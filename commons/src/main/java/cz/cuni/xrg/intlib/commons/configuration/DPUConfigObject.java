package cz.cuni.xrg.intlib.commons.configuration;

import java.io.Serializable;

/**
 * Base interface for dpu's configuration.
 *
 * @author Petyr
 */
public interface DPUConfigObject extends Serializable {
	
	/**
	 * Check if configuration is valid ie. can be used to configure
	 * DPU or configuration dialog.
	 * @return
	 */
	boolean isValid();
	
}
