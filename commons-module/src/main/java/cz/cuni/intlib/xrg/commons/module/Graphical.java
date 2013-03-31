package cz.cuni.intlib.xrg.commons.module;

import com.vaadin.ui.CustomComponent;

/**
 * Interface with graphical configuration dialog.
 * @author Petyr
 *
 */
public interface Graphical {

	/**
	 * Return configuration component for class. The configuration
	 * component must not contains close button. The component will be closed
	 * by the application. 
	 * @return configuration component.
	 */
	public CustomComponent getConfigurationComponent();

}
