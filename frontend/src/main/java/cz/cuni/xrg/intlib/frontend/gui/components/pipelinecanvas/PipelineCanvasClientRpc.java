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
	 * @param dpuId
	 * @param name
	 * @param description
	 * @param posX
	 * @param posY
	 */
	public void addNode(int dpuId, String name, String description, int posX, int posY);

	/**
	 * Adds new edge on graph canvas.
	 * @param connId
	 * @param dpuFrom
	 * @param dpuTo
	 */
	public void addEdge(int connId, int dpuFrom, int dpuTo);

	/**
	 * Initializes javascript part of graph canvas component.
	 */
	public void init();

	/**
	 * Updates information of given node's DPUInstance.
	 * @param id
	 * @param name
	 * @param description
	 */
	public void updateNode(int id, String name, String description);
	
	/**
	 * Resizes stage.
	 * @param height new height
	 * @param width new width
	 */
	public void resizeStage(int height, int width);
	
	/**
	 * Zooms the stage to given ratio.
	 * @param zoom 
	 */
	public void zoomStage(double zoom);

}
