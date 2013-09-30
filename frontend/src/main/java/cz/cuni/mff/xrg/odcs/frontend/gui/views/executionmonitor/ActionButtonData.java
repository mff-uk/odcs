package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor;

/**
 * Preserves buttons type and table row on which the button on Execution Monitor
 * was pressed. Parameters String action and Object data are setting in
 * {@link GenerateActionColumnMonitor} after button click event in
 * {@link ExecutionMonitor} this data will be get.
 *
 * @author Maria Kukhar
 */
public class ActionButtonData {

	public String action;
	public Object data;

	public ActionButtonData(String buttonAction, Object data) {
		this.action = buttonAction;
		this.data = data;
	}
}
