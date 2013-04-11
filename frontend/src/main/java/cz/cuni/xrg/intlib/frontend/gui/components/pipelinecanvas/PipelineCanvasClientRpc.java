package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Interface for RPC calls to client = JS part of component.
 * @author Bogo
 */
public interface PipelineCanvasClientRpc extends ClientRpc {

	//public void loadPipeline(Pipeline pipeline);

	public void addNode(int dpuId, String name, String description, int posX, int posY);

	public void addEdge(int connId, int dpuFrom, int dpuTo);

	public void init();

	public void updateNode(int id, String name, String description);

}
