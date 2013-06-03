package cz.cuni.xrg.intlib.commons.web;

import com.vaadin.ui.CustomComponent;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;

/**
 * Interface with graphical configuration dialog.
 * @author Petyr
 *
 */
public interface DialogProvider <C extends Configuration> {

	/**
	 * Return configuration dialog.
	 * @return
	 */
	public AbstractConfigurationDialog<C> getConfigurationDialog();
	
	/**
	 * Return configuration component for class. The configuration
	 * component must not contains close button. The component will be closed
	 * by the application. 
	 * @param configuration Default configuration shown in dialog. 
	 * @return configuration component.
	 */
	@Deprecated
	public CustomComponent getConfigurationComponent(Configuration configuration);

}
