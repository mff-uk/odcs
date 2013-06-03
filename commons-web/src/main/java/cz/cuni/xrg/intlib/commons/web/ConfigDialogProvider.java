package cz.cuni.xrg.intlib.commons.web;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;

/**
 * Interface with graphical configuration dialog.
 * @author Petyr
 *
 */
public interface ConfigDialogProvider <C extends Configuration> {

	/**
	 * Return configuration dialog.
	 * @return
	 */
	public AbstractConfigDialog<C> getConfigurationDialog();

}
