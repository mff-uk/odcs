package cz.cuni.intlib.frontend;

import com.vaadin.server.VaadinRequest;

import cz.cuni.intlib.frontend.gui.MainLayout;

/**
 * Frontend application entry point.
 * @author Petyr
 *
 */
public class AppEntry extends com.vaadin.ui.UI {
				
	@Override
	protected void init(VaadinRequest request) {
		// create application instance
		AppInstance.createInstance();
		
		// create application layout
		MainLayout main = new MainLayout();
		// set application content
		setContent(main);
	}
	
}
