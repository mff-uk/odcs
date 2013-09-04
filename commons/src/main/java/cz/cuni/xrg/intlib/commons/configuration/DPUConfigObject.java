package cz.cuni.xrg.intlib.commons.configuration;

import java.io.Serializable;

/**
 * Base interface for dpu's configuration. <b>All the configuration object must
 * provide public parameter less constructor!</b>
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

	/**
	 * Return text that should be used as a DPU tool tip. The text should
	 * contains information about configuration. If the configuration is not
	 * valid, or this functionality is not supported can return null.
	 * 
	 * @return Can be null.
	 */
	// String getToolTip();

	/**
	 * Return configuration summary that can be used as DPU description. The
	 * summary should be short and as much informative as possible. Return null
	 * in case of invalid configuration or it this functionality is not
	 * supported
	 * 
	 * @return Can be null.
	 */
	// String getDescription();
}
