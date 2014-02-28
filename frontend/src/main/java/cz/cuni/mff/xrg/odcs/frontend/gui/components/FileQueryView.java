package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Label;

/**
 *
 * @author Bogo
 */
public class FileQueryView extends QueryView {
	
	/**
	 * Constructor.
	 */
	public FileQueryView() {
		Label label = new Label("Browser not available.");
		setCompositionRoot(label);
	}

	@Override
	public void browseDataUnit() {
		
	}

	@Override
	public void setQueryingEnabled(boolean enabled) {
		
	}

	@Override
	public void reset() {
		
	}
	
}
