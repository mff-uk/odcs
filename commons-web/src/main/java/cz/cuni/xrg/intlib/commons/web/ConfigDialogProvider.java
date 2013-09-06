package cz.cuni.xrg.intlib.commons.web;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Interface which provides graphical configuration dialog associated with the given DPU
 * 
 * @author Petyr
 *
 */
public interface ConfigDialogProvider <C extends DPUConfigObject> {

	/**
	 * Return configuration dialog.
	 * @return
	 */
	public AbstractConfigDialog<C> getConfigurationDialog();

}
