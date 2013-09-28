
package cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas;

import com.vaadin.ui.Component;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Listener for debug request on {@link PipelineCanvas}.
 *
 * @author Bogo
 */
public interface ShowDebugListener extends Component.Listener {
	
	/**
	 * Inform, that debug was requested for given {@link Pipeline} and {@link Node}.
	 * @param pipeline {@link Pipeline} to debug.
	 * @param debugNode {@link Node} where debug should end.
	 */
	void showDebug(Pipeline pipeline, Node debugNode);
}
