package cz.cuni.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Interface for RPC calls to client = JS part of component.
 * @author Bogo
 */
public interface PipelineCanvasClientRpc extends ClientRpc {

	//public void loadPipeline(Pipeline pipeline);

	public void addDpu(int dpuId, String name, String description, int posX, int posY);

	public void addConnection(int connId, int dpuFrom, int dpuTo);

	public void init();

	public int[] getDpuPosition(int id);

}
