package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.CloseEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Position;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibHelper;
import cz.cuni.xrg.intlib.frontend.gui.components.DPUDetail;
import cz.cuni.xrg.intlib.frontend.gui.components.EdgeDetail;
import java.util.Collection;
import java.util.EventObject;
import java.util.Stack;

/**
 * Component for visualization of the pipeline.
 *
 * @author Bogo
 */
@SuppressWarnings("serial")
@JavaScript({"js_pipelinecanvas.js", "kinetic-v4.5.4.min.js", "jquery-2.0.0.min.js"})
public class PipelineCanvas extends AbstractJavaScriptComponent {

	final int DPU_WIDTH = 120;
	final int DPU_HEIGHT = 100;
	int dpuCount = 0;
	int connCount = 0;
	float currentZoom = 1.0f;
	private PipelineGraph graph;
	private Pipeline pip;
	//private Stack<PipelineGraph> historyStack;
	private Stack<DPUInstanceRecord> dpusToDelete = new Stack<>();

	/**
	 * Initial constructor with registering of server side RPC.
	 */
	public PipelineCanvas() {
		//this.historyStack = new Stack();

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
				storeHistoryGraph();
				graph.removeEdge(connectionId);
			}

			@Override
			public void onConnectionAdded(int dpuFrom, int dpuTo) {
				addConnection(dpuFrom, dpuTo);
			}

			@Override
			public void onDpuRemoved(int dpuId) {
				storeHistoryGraph();
				Node removedNode = graph.removeDpu(dpuId);
				dpusToDelete.add(removedNode.getDpuInstance());
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

			@Override
			public void onDataUnitNameEditRequested(int edgeId) {
				Edge edge = graph.getEdgeById(edgeId);
				showEdgeDetail(edge);
			}
		});

	}

	/**
	 * Start pipeline in debug mode and show debug window.
	 *
	 * @param dpuId {@Link int} id of dpu, where debug should end.
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	private void showDebugWindow(int dpuId) throws IllegalArgumentException, NullPointerException {
		//TODO: Debug
		pip.setGraph(graph);
		App.getApp().getPipelines().save(pip);
		Node debugNode = graph.getNodeById(dpuId);
		PipelineExecution pExec = IntlibHelper.runPipeline(pip, true, debugNode);
		if (pExec == null) {
                        //Solved by dialog if backend is offline in method runPipeline.
			//Notification.show("Pipeline execution failed!", Notification.Type.ERROR_MESSAGE);
			return;
		}

		DPUInstanceRecord debugDpu = debugNode.getDpuInstance();
		fireShowDebug(pExec, debugDpu);
	}

	/**
	 * Method initializing client side RPC.
	 */
	public void init() {
		getRpcProxy(PipelineCanvasClientRpc.class).init();
	}

	/**
	 * Method updating node position on server side.
	 *
	 * @param dpuId Id of {@link Node} which was moved.
	 * @param newX New X coordinate.
	 * @param newY New Y coordinate.
	 */
	private void dpuMoved(int dpuId, int newX, int newY) {
		graph.moveNode(dpuId, newX, newY);
	}

	/**
	 * Adds new DPUTemplateRecord to graph canvas.
	 *
	 * @param dpu Id of {@link DPUTemplateRecord} which should be added.
	 * @param x X coordinate of position, where dpu should be added.
	 * @param y Y coordinate of position, where dpu should be added.
	 */
	public void addDpu(DPUTemplateRecord dpu, int x, int y) {
		DPUInstanceRecord dpuInstance = App.getDPUs().createInstanceFromTemplate(dpu);
		Node node = graph.addDpuInstance(dpuInstance);
		getRpcProxy(PipelineCanvasClientRpc.class)
				.addNode(node.hashCode(), dpu.getName(), dpu.getDescription(), dpu.getType().name(), (int) (x / currentZoom), (int) (y / currentZoom));
	}

	/**
	 * Adds new edge to graph canvas.
	 *
	 * @param dpuFrom Id of Node, where edge starts.
	 * @param dpuTo Id of Node, where edge ends.
	 */
	public void addConnection(int dpuFrom, int dpuTo) {
		String result = graph.validateNewEdge(dpuFrom, dpuTo);
		if (result == null) {
			int connectionId = graph.addEdge(dpuFrom, dpuTo);
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(connectionId, dpuFrom, dpuTo, null);
		} else {
			Notification.show("Adding edge failed", result, Notification.Type.WARNING_MESSAGE);
		}

	}

	/**
	 * Shows given pipeline on graph canvas.
	 *
	 * @param pipeline {@link Pipeline} to show on graph canvas.
	 */
	public void showPipeline(Pipeline pipeline) {
		this.pip = pipeline;
		setGraph(pipeline.getGraph());
	}

	/**
	 * Initializes the canvas with given graph.
	 *
	 * @param pg {@link PipelineGraph} to show on canvas.
	 */
	private void setGraph(PipelineGraph pg) {
		if (this.graph != null) {
			getRpcProxy(PipelineCanvasClientRpc.class).clearStage();
		}
		this.graph = pg;

		for (Node node : graph.getNodes()) {
			DPUInstanceRecord dpu = node.getDpuInstance();
			getRpcProxy(PipelineCanvasClientRpc.class).addNode(node.hashCode(), dpu.getName(), dpu.getDescription(), dpu.getType().name(), node.getPosition().getX(), node.getPosition().getY());
		}
		for (Edge edge : graph.getEdges()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(edge.hashCode(), edge.getFrom().hashCode(), edge.getTo().hashCode(), edge.getDataUnitName());
		}
	}

	/**
	 * Saves graph from graph canvas.
	 *
	 * @param pipeline {@link Pipeline} where graph should be saved.
	 */
	public void saveGraph(Pipeline pipeline) {
		for(DPUInstanceRecord instance : dpusToDelete) {
			App.getDPUs().delete(instance);
		}
		pipeline.setGraph(graph);
	}

	@Override
	protected PipelineCanvasState getState() {
		return (PipelineCanvasState) super.getState();
	}

	/**
	 * Shows detail of given {@link DPUInstance} in new sub-window.
	 *
	 * @param node {@link Node} containing DPU, which detail should be showed.
	 */
	public void showDPUDetail(final Node node) {
		final DPUInstanceRecord dpu = node.getDpuInstance();
		DPUDetail detailDialog = new DPUDetail(dpu);
		detailDialog.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				fireDetailClosed(Node.class);
				getRpcProxy(PipelineCanvasClientRpc.class).updateNode(node.hashCode(), dpu.getName(), dpu.getDescription());
			}
		});
		App.getApp().addWindow(detailDialog);
	}

	/**
	 * Shows detail of given {@link Edge} in new sub-window.
	 * @param edge {@link Edge} which detail should be showed.
	 */
	private void showEdgeDetail(final Edge edge) {
		EdgeDetail edgeDetailDialog = new EdgeDetail(edge);
		edgeDetailDialog.addCloseListener(new Window.CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				//fireDetailClosed(Edge.class);
				getRpcProxy(PipelineCanvasClientRpc.class).updateEdge(edge.hashCode(), edge.getDataUnitName());
			}
		});
		App.getApp().addWindow(edgeDetailDialog);
	}

	

	/**
	 * Inform listeners, that the detail is closing.
	 */
	protected void fireDetailClosed(Class klass) {
		Collection<Listener> ls = (Collection<Listener>) this.getListeners(Component.Event.class);
		for (Listener l : ls) {
			try {
				DetailClosedListener dcl = (DetailClosedListener) l;
				dcl.detailClosed(new EventObject(klass));
			} catch (Exception ex) {
				//TODO: Solve better!
			}
		}
	}

	/**
	 * Inform listeners, that debug request was made.
	 *
	 * @param execution {@link PipelineExecution} representing current debug
	 * session.
	 * @param instance {@link DPUInstanceRecord} where execution should end.
	 */
	protected void fireShowDebug(PipelineExecution execution, DPUInstanceRecord instance) {
		Collection<Listener> ls = (Collection<Listener>) this.getListeners(Component.Event.class);
		for (Listener l : ls) {
			try {
				ShowDebugListener sdl = (ShowDebugListener) l;
				sdl.showDebug(execution, instance);
			} catch (Exception ex) {
				//TODO: Solve better!
			}
		}
	}

	/**
	 * Change canvas size.
	 *
	 * @param height New height of canvas in pixels.
	 * @param width New width of canvas in pixels.
	 */
	public void resizeCanvas(int height, int width) {
		getRpcProxy(PipelineCanvasClientRpc.class).resizeStage(height, width);
	}

	/**
	 * Zoom the canvas.
	 *
	 * @param isZoomIn +/- zoom.
	 * @return {@link Position} with new size of canvas.
	 */
	public Position zoom(boolean isZoomIn) {
		Position bounds = new Position(0, 0);
		if (graph != null) {
			bounds = graph.getBounds();
		}
		bounds.setX(bounds.getX() + DPU_WIDTH);
		bounds.setY(bounds.getY() + DPU_HEIGHT);
		if (isZoomIn && currentZoom < 2) {
			if (currentZoom < 1.5) {
				currentZoom = 1.5f;
			} else {
				currentZoom = 2.0f;
			}
		} else if (!isZoomIn && currentZoom > 1.0f) {
			if (currentZoom > 1.5f) {
				currentZoom = 1.5f;
			} else {
				currentZoom = 1.0f;
			}
		}
		getRpcProxy(PipelineCanvasClientRpc.class).zoomStage(currentZoom);
		bounds.setX((int) (bounds.getX() * currentZoom));
		bounds.setY((int) (bounds.getY() * currentZoom));
		//return bounds;
		return new Position((int) (1600 * currentZoom), (int) (630 * currentZoom));
	}

	/**
	 * Undo changes on canvas.
	 *
	 */
	public void undo() {
//		if (!historyStack.isEmpty()) {
//			PipelineGraph restoredGraph = historyStack.pop();
//			
//		}
	}

	/**
	 * Store graph in stack for undo.
	 */
	private void storeHistoryGraph() {
		//PipelineGraph clonedGraph = SerializationUtils.clone(graph);
		//historyStack.push(clonedGraph);
	}
}
