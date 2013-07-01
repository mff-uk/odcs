package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Interface for RPC calls to client = JS part of component.
 * @author Bogo
 */
public interface PipelineCanvasClientRpc extends ClientRpc {

	//public void loadPipeline(Pipeline pipeline);

	/**
	 * Adds new node on graph canvas.
	 * @param dpuId Id of node.
	 * @param name Name of dpu.
	 * @param description Description of dpu.
	 * @param type {@link String} with type of dpu.
	 * @param posX X coordinate of node.
	 * @param posY Y coordinate of node.
	 */
	public void addNode(int dpuId, String name, String description, String type, int posX, int posY);

	/**
	 * Adds new edge on graph canvas.
	 * @param connId Id of edge.
	 * @param dpuFrom Id of start node.
	 * @param dpuTo Id of end node.
	 */
	public void addEdge(int connId, int dpuFrom, int dpuTo);

	/**
	 * Initializes JS part of graph canvas component.
	 */
	public void init();

	/**
	 * Updates information of given node's DPUInstance.
	 * @param id Id of node to update.
	 * @param name New name of corresponding dpu.
	 * @param description New description of corresponding dpu.
	 */
	public void updateNode(int id, String name, String description);
	
	/**
	 * Resizes stage.
	 * @param height New height.
	 * @param width New width.
	 */
	public void resizeStage(int height, int width);
	
	/**
	 * Zooms the stage to given ratio.
	 * @param zoom {@link double} with ratio.(1.0 - 2.0)
	 */
	public void zoomStage(double zoom);

	/**
	 * Clears stage.
	 */
	public void clearStage();

}
