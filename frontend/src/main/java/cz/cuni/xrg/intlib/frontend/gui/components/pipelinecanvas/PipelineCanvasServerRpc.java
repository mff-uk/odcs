package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Interface for calling RPC on server from client = JS part of component.
 * @author Bogo
 */
public interface PipelineCanvasServerRpc extends ServerRpc {

	/**
	 * Occurs when new edge is created on graph canvas.
	 * @param dpuFrom
	 * @param dpuTo
	 */
	public void onConnectionAdded(int dpuFrom, int dpuTo);

	/**
	 * Occurs when edge is removed from graph canvas.
	 * @param connectionId
	 */
	public void onConnectionRemoved(int connectionId);

	/**
	 * Occurs when detail of given DPUInstance is requested.
	 * @param dpuId
	 */
	public void onDetailRequested(int dpuId);

	/**
	 * Occurs when DPUInstance is removed from graph canvas.
	 * @param dpuId
	 */
	public void onDpuRemoved(int dpuId);

	/**
	 * Occurs when node on graph canvas is moved.
	 * @param dpuId
	 * @param newX
	 * @param newY
	 */
	public void onDpuMoved(int dpuId, int newX, int newY);

	/**
	 * Occurs on logging a message from graph canvas.
	 * @param message
	 */
	public void onLogMessage(String message);

	/**
	 * Occurs when debug up to given DPURecord is requested.
	 * @param dpuId
	 */
	public void onDebugRequested(int dpuId);
}
