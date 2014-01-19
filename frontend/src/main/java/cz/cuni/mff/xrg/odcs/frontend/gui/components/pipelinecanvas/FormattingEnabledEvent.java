
package cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 * Event for enabling/disabling of formatting tool bar.
 *
 * @author Bogo
 */
public class FormattingEnabledEvent extends Event {
	
	private boolean isEnabled;
	
	public FormattingEnabledEvent(Component source, boolean isEnabled) {
		super(source);
		this.isEnabled = isEnabled;
	}

	public boolean isEnabled() {
		return isEnabled;
	}
}
