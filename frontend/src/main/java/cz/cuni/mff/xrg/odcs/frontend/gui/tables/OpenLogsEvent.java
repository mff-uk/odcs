package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 * Event for passing request to show logs with preselected DPU.
 *
 * @author Bogo
 */
public class OpenLogsEvent extends Event {
	
	private Long dpuId;
	
	public OpenLogsEvent(Component source, Long dpuId) {
		super(source);
		this.dpuId = dpuId;
	}
	
	public Long getDpuId() {
		return dpuId;
	}
	
}
