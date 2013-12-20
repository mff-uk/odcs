package cz.cuni.mff.xrg.odcs.commons.web;

import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;

/**
 * Interface which provides graphical configuration dialog associated with the given DPU
 * 
 * @author Petyr
 * @param <C>
 *
 */
public interface ConfigDialogProvider <C extends DPUConfigObject> {

	/**
	 * Return configuration dialog.
	 * @return
	 */
	public AbstractConfigDialog<C> getConfigurationDialog();

}
