package cz.cuni.xrg.intlib.commons.web;

import cz.cuni.xrg.intlib.commons.configuration.Config;

/**
 * Interface with graphical configuration dialog.
 * 
 * @author Petyr
 *
 */
public interface ConfigDialogProvider <C extends Config> {

	/**
	 * Return configuration dialog.
	 * @return
	 */
	public AbstractConfigDialog<C> getConfigurationDialog();

}
