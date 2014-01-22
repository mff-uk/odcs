package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.CustomComponent;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;

/**
 *
 * @author Bogo
 */
public abstract class QueryView extends CustomComponent {

	private DataUnitInfo dataUnitInfo;
	private ExecutionInfo executionInfo;
	private DPUInstanceRecord selectedDpu;

	public abstract void browseDataUnit();

	public abstract void setQueryingEnabled(boolean b);

	public abstract void reset();

	public void setExecutionInfo(ExecutionInfo executionInfo) {
		this.executionInfo = executionInfo;
	}

	public void setDataUnitInfo(DataUnitInfo duInfo) {
		dataUnitInfo = duInfo;
	}

	public void setSelectedDpu(DPUInstanceRecord dpu) {
		selectedDpu = dpu;
	}

	protected ExecutionInfo getExecutionInfo() {
		return executionInfo;
	}

	protected DataUnitInfo getDataUnitInfo() {
		return dataUnitInfo;
	}

	protected DPUInstanceRecord getSelectedDpu() {
		return selectedDpu;
	}
}
