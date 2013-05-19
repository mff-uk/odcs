package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import cz.cuni.xrg.intlib.commons.app.communication.Client;
import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfiguration;
import cz.cuni.xrg.intlib.commons.app.conf.ConfProperty;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.components.DPUDetail;
import cz.cuni.xrg.intlib.frontend.gui.components.DebuggingView;

/**
 * Component for visualization of the pipeline.
 *
 * @author Bogo
 */
@SuppressWarnings("serial")
@JavaScript({"js_pipelinecanvas.js", "kinetic-v4.4.3.min.js", "jquery-2.0.0.min.js"})
public class PipelineCanvas extends AbstractJavaScriptComponent {

	int dpuCount = 0;
	int connCount = 0;
	private PipelineGraph graph;

	//TEMPORARY
	private Pipeline pip;

	/**
	 * Initial constructor with registering of server side RPC.
	 */
	public PipelineCanvas() {

		this.setId("container");
		//this.setWidth(1500,  Unit.PIXELS);
		//this.setHeight(800, Unit.PIXELS);
		this.setStyleName("pipelineContainer");

		registerRpc(new PipelineCanvasServerRpc() {

			@Override
			public void onDetailRequested(int dpuId) {
				// TODO Auto-generated method stub
				// propably publish event one level higher
				Node node = graph.getNodeById(dpuId);
				if (node != null) {
					showDPUDetail(node);
				}
			}

			@Override
			public void onConnectionRemoved(int connectionId) {
				graph.removeEdge(connectionId);
			}

			@Override
			public void onConnectionAdded(int dpuFrom, int dpuTo) {
				addConnection(dpuFrom, dpuTo);
			}

			@Override
			public void onDpuRemoved(int dpuId) {
				Node removedNode = graph.removeDpu(dpuId);
				App.getDPUs().delete(removedNode.getDpuInstance());

			}

			@Override
			public void onDpuMoved(int dpuId, int newX, int newY) {
				dpuMoved(dpuId, newX, newY);
			}

			@Override
			public void onLogMessage(String message) {
				//TODO: Log JS messages
			}

			@Override
			public void onDebugRequested(int dpuId) {
				showDebugWindow(dpuId);
			}
		});
	}

	private void showDebugWindow(int dpuId) throws IllegalArgumentException, NullPointerException {
		//TODO: Debug
		pip.setGraph(graph);
		PipelineExecution pExec = runPipeline(pip, true);
		if(pExec == null) {
			Notification.show("Pipeline execution failed!", Notification.Type.ERROR_MESSAGE);
			return;
		}

		DPUInstance debugDpu = graph.getNodeById(dpuId).getDpuInstance();
		DebuggingView dv = new DebuggingView(pExec, debugDpu);
		Window debugWindow = new Window("Debug window", dv);
		debugWindow.addCloseListener(new Window.CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
			}
		});
		App.getApp().addWindow(debugWindow);
	}

	/**
	 * Method initializing client side RPC.
	 */
	public void init() {
		getRpcProxy(PipelineCanvasClientRpc.class).init();
	}

	private void dpuMoved(int dpuId, int newX, int newY) {
		graph.moveNode(dpuId, newX, newY);
	}

	/**
	 * Adds new DPU to graph canvas.
	 *
	 * @param dpu
	 * @param x
	 * @param y
	 */
	public void addDpu(DPU dpu, int x, int y) {
		int dpuInstanceId = graph.addDpu(dpu);
		getRpcProxy(PipelineCanvasClientRpc.class).addNode(dpuInstanceId, dpu.getName(), dpu.getDescription(), x, y);
	}

	/**
	 * Adds new edge to graph canvas.
	 *
	 * @param dpuFrom
	 * @param dpuTo
	 */
	public void addConnection(int dpuFrom, int dpuTo) {
		String result = graph.validateNewEdge(dpuFrom, dpuTo);
		if(result == null) {
			int connectionId = graph.addEdge(dpuFrom, dpuTo);
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(connectionId, dpuFrom, dpuTo);
		} else {
			 Notification.show("Adding edge failed", result, Notification.Type.WARNING_MESSAGE);
		}

	}

	/**
	 * Shows given pipeline on graph canvas.
	 *
	 * @param pipeline
	 */
	public void showPipeline(Pipeline pipeline) {
		this.graph = pipeline.getGraph();
		this.pip = pipeline;
		for (Node node : graph.getNodes()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addNode(node.hashCode(), node.getDpuInstance().getName(), node.getDpuInstance().getDescription(), node.getPosition().getX(), node.getPosition().getY());
		}
		for (Edge edge : graph.getEdges()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(edge.hashCode(), edge.getFrom().hashCode(), edge.getTo().hashCode());
		}
	}

	/**
	 * Saves graph from graph canvas.
	 *
	 * @param pipeline
	 */
	public void saveGraph(Pipeline pipeline) {
//		for(DpuInstance dpu : pipeline.getDpus()) {
//			int[] position = getRpcProxy(PipelineCanvasClientRpc.class).getDpuPosition(dpu.Id);
//			dpu.setX(position[0]);
//			dpu.setY(position[1]);
//		}
		//pipeline.setWidth(Math.round(this.getWidth()));
		//pipeline.setHeight(Math.round(this.getHeight()));

		pipeline.setGraph(graph);
	}

	@Override
	protected PipelineCanvasState getState() {
		return (PipelineCanvasState) super.getState();
	}

	/**
	 * Shows detail of given DPUInstance in new sub-window
	 *
	 * @param dpu
	 */
	public void showDPUDetail(final Node node) {
		final DPUInstance dpu = node.getDpuInstance();
		DPUDetail detailDialog = new DPUDetail(dpu);
		detailDialog.addCloseListener(new Window.CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				getRpcProxy(PipelineCanvasClientRpc.class).updateNode(node.hashCode(), dpu.getName(), dpu.getDescription());
			}
		});
		App.getApp().addWindow(detailDialog);

	}

	public PipelineExecution runPipeline(Pipeline pipeline, boolean inDebugMode) {
		PipelineExecution pipelineExec =  new PipelineExecution(pipeline);
		pipelineExec.setDebugging(inDebugMode);
		// TODO Petyr: leave null value?
		pipelineExec.setWorkingDirectory("");
		// do some settings here

		// store into DB
		App.getPipelines().save(pipelineExec);
		AppConfiguration config = App.getApp().getAppConfiguration();
		Client client = new Client(
			config.getString(ConfProperty.BACKEND_HOST),
			config.getInteger(ConfProperty.BACKEND_PORT)
		);

		// send message to backend
		try {
			client.checkDatabase();
		} catch (CommunicationException e) {
			Notification.show("Error", "Can't connect to backend. Exception: " + e.getCause().getMessage(),
					Notification.Type.ERROR_MESSAGE);
			return null;
		}

		// show message about action
		Notification.show("pipeline execution started ..",
				Notification.Type.HUMANIZED_MESSAGE);

		return pipelineExec;
	}
}
