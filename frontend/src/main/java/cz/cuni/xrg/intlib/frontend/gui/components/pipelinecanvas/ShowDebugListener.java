
package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.ui.Component;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;

/**
 *
 * @author Bogo
 */
public interface ShowDebugListener extends Component.Listener {
	void showDebug(PipelineExecution execution, DPUInstanceRecord instance);
}
