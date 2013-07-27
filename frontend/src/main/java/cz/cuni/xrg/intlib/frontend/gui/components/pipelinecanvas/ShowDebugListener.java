
package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.ui.Component;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Listener for debug request on {@link PipelineCanvas}.
 *
 * @author Bogo
 */
public interface ShowDebugListener extends Component.Listener {
	
	/**
	 * Inform, that debug was requested for given {@link PipelineExecution} and {@link DPUInstanceRecord}.
	 * @param execution {@link PipelineExecution} to debug.
	 * @param instance {@link DPUInstanceRecord} where debug should end.
	 */
	void showDebug(PipelineExecution execution, DPUInstanceRecord instance);
}
