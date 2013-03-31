package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Interface for calling RPC on server from client = JS part of component.
 * @author Bogo
 */
public interface PipelineCanvasServerRpc extends ServerRpc {

	public void onConnectionAdded(int dpuFrom, int dpuTo);

	public void onConnectionRemoved(int connectionId);

	public void onDetailRequested(int dpuId);

	public void onDpuRemoved(int dpuId);
}
