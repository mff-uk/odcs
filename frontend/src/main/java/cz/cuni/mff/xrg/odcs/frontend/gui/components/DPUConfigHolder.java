package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Component for holding the DPU's configuration dialog. The component
 * show message if the configuration component is null.
 *
 * @author Škoda Petr
 */
public class DPUConfigHolder extends CustomComponent {

	public DPUConfigHolder() {
		setHeight("100%");
		setWidth("100%");
	}

	/**
	 * Set the component representing the dialog.
	 *
	 * @param confDialog Can be null.
	 */
	public void setConfigComponent(Component confDialog) {
		if (confDialog == null) {
			// create label
			Label infoLabel = new Label();
			infoLabel.setSizeUndefined();
			infoLabel.setValue("This DPU doesn't provide configuration component.");
			
			VerticalLayout layout = new VerticalLayout();
			layout.setWidth("100%");
			layout.setHeight("100%");			
			layout.addComponent(infoLabel);
			layout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);

			setCompositionRoot(layout);
		} else {
			confDialog.setWidth("100%");
			confDialog.setHeight("100%");
			setCompositionRoot(confDialog);
		}
	}

}
