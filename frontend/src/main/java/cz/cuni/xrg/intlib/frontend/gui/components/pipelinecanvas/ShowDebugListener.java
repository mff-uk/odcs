
package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.ui.Component;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 *
 * @author Bogo
 */
public interface ShowDebugListener extends Component.Listener {
	void showDebug(PipelineExecution execution, DPUInstance instance);
}
