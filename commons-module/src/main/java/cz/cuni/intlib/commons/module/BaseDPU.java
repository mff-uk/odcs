package cz.cuni.intlib.commons.module;

import com.vaadin.ui.CustomComponent;

/**
 * Add vaadin support to BaseDPU.
 * @author Petyr
 *
 */
public interface BaseDPU extends cz.cuni.intlib.commons.BaseDPU {

	/**
	 * Return configuration component for DPU. The configuration
	 * component must not contains close button. The component will be closed
	 * by application. 
	 * @return DPU's configuration component.
	 */
	public CustomComponent getConfigurationComponent();

}
