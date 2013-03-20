package cz.cuni.mff.ms.intlib.frontend;

import com.vaadin.server.VaadinRequest;

import cz.cuni.mff.ms.intlib.frontend.gui.MainLayout;

/**
 * Frontend application entry point.
 * @author Petyr
 *
 */
public class AppEntry extends com.vaadin.ui.UI {
				
	@Override
	protected void init(VaadinRequest request) {		
		MainLayout main = new MainLayout();
		setContent(main);
	}
	
}
