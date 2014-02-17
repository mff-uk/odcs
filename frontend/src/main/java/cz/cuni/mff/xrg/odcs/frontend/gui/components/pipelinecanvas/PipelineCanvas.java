package cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.CloseEvent;

import cz.cuni.mff.xrg.odcs.commons.app.data.EdgeCompiler;
import cz.cuni.mff.xrg.odcs.commons.app.data.EdgeFormater;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUExplorer;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Position;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.PipelineValidator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.PipelineValidator.PipelineValidationException;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUDetail;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.EdgeDetail;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PipelineEdit;

import java.util.Collection;
import java.util.List;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Component for visualization of the pipeline.
 *
 * @author Bogo
 */
@Component
@Scope("prototype")
@SuppressWarnings("serial")
@JavaScript({"js_pipelinecanvas.js", "kinetic-v4.5.4.min.js", "jquery-2.0.0.min.js"})
public class PipelineCanvas extends AbstractJavaScriptComponent {

	final int DPU_WIDTH = 120;
	final int DPU_HEIGHT = 100;
	int dpuCount = 0;
	int connCount = 0;
	float currentZoom = 1.0f;
	private PipelineGraph graph;
	private Stack<PipelineGraph> historyStack;
	private Stack<DPUInstanceRecord> dpusToDelete = new Stack<>();
	private boolean isModified = false;
	@Autowired
	private DPUExplorer dpuExplorer;
	@Autowired
	private PipelineValidator pipelineValidator;
	@Autowired
	private DPUFacade dpuFacade;
	private static final Logger LOG = LoggerFactory.getLogger(PipelineCanvas.class);
	private DPUDetail detailDialog;
	private Window.CloseListener detailCloseListener;
	private String canvasMode = PipelineEdit.DEVELOP_MODE;
	private EdgeFormater edgeFormater = new EdgeFormater();
	/**
	 * Initial constructor with registering of server side RPC.
	 */
	public PipelineCanvas() {
		this.historyStack = new Stack();

		this.setId("container");
		this.setStyleName("pipelineContainer");

		registerRpc(new PipelineCanvasServerRpc() {
			@Override
			public void onDetailRequested(int dpuId) {
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
				storeHistoryGraph();
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
				//storeHistoryGraph();
				isModified = true;
				fireEvent(new GraphChangedEvent(PipelineCanvas.this, false));
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

			@Override
			public void onStoreHistory() {
				storeHistoryGraph();
			}

			@Override
			public void onDpuCopyRequested(int dpuId, int x, int y) {
				storeHistoryGraph();
				copyDpu(dpuId, x, y);
			}

			@Override
			public void onMultipleDPUsSelected(boolean selected) {
				fireEvent(new FormattingEnabledEvent(PipelineCanvas.this, selected));
			}
		});

	}

	/**
	 * Method initializing client side RPC.
	 */
	public void init() {
		detailDialog = new DPUDetail(dpuFacade);
		getRpcProxy(PipelineCanvasClientRpc.class).init();
	}

	/**
	 * Saves graph from graph canvas.
	 *
	 * @param pipeline {@link Pipeline} where graph should be saved.
	 * @return If after save clean up is needed.
	 */
	public boolean saveGraph(Pipeline pipeline) {
		historyStack.clear();

		pipeline.setGraph(graph);
		isModified = false;
		return !dpusToDelete.isEmpty();
	}

	/**
	 * Cleans up removed DPU Instances, as Nodes dependency doesn't take care of
	 * this. Always call after saving pipeline if saveGraph return True.
	 *
	 */
	public void afterSaveCleanUp() {
		for (DPUInstanceRecord instance : dpusToDelete) {
			dpuFacade.delete(instance);
		}
		dpusToDelete.clear();
	}

	/**
	 * Adds new DPUTemplateRecord to graph canvas.
	 *
	 * @param dpu Id of {@link DPUTemplateRecord} which should be added.
	 * @param x X coordinate of position, where dpu should be added.
	 * @param y Y coordinate of position, where dpu should be added.
	 */
	public void addDpu(DPUTemplateRecord dpu, int x, int y) {
		storeHistoryGraph();
		DPUInstanceRecord dpuInstance = dpuFacade.createInstanceFromTemplate(dpu);
		Node node = graph.addDpuInstance(dpuInstance);
		getRpcProxy(PipelineCanvasClientRpc.class)
				.addNode(node.hashCode(), dpu.getName(), dpu.getDescription(), dpu.getType().name(), (int) (x / currentZoom), (int) (y / currentZoom), true);
	}

	/**
	 * Adds new edge to graph canvas.
	 *
	 * @param dpuFrom Id of Node, where edge starts.
	 * @param dpuTo Id of Node, where edge ends.
	 */
	public void addConnection(int dpuFrom, int dpuTo) {
		String result = graph.validateNewEdge(dpuFrom, dpuTo);
		Node to = graph.getNodeById(dpuTo);
		if (result == null) {
			if (dpuExplorer.getInputs(to.getDpuInstance()).isEmpty()) {
				result = "Target DPU has no inputs.";
			}
		}
		if (result == null) {
			int connectionId = graph.addEdge(dpuFrom, dpuTo);
			EdgeCompiler edgeCompiler = new EdgeCompiler();
			Edge edge = graph.getEdgeById(connectionId);
			DPUInstanceRecord from = graph.getNodeById(dpuFrom).getDpuInstance();
			edgeCompiler.setDefaultMapping(edge, dpuExplorer.getOutputs(from), dpuExplorer.getInputs(to.getDpuInstance()));

			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(connectionId, dpuFrom, dpuTo, edgeFormater.format(edge.getScript()));
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
		setGraph(pipeline.getGraph());
	}

	/**
	 * Shows detail of given {@link DPUInstance} in new sub-window.
	 *
	 * @param node {@link Node} containing DPU, which detail should be showed.
	 */
	public void showDPUDetail(final Node node) {
		final DPUInstanceRecord dpu = node.getDpuInstance();
		detailDialog.showDpuDetail(dpu, canvasMode.equals(PipelineEdit.STANDARD_MODE));
		if (detailCloseListener != null) {
			detailDialog.removeCloseListener(detailCloseListener);
		}
		detailCloseListener = new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				DPUDetail source = (DPUDetail) e.getSource();
				if (source.getResult()) {
					isModified = true;
					fireEvent(new DetailClosedEvent(PipelineCanvas.this, Node.class));
					getRpcProxy(PipelineCanvasClientRpc.class).updateNode(node.hashCode(), dpu.getName(), dpu.getDescription());
					boolean isValid = pipelineValidator.checkDPUValidity(dpu);
					getRpcProxy(PipelineCanvasClientRpc.class).setDpuValidity(node.hashCode(), isValid);
				}
			}
		};

		detailDialog.addCloseListener(detailCloseListener);
		if (!UI.getCurrent().getWindows().contains(detailDialog)) {
			UI.getCurrent().addWindow(detailDialog);
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
	 * @return History stack contains another graph.
	 */
	public boolean undo() {
		if (!historyStack.isEmpty()) {
			PipelineGraph restoredGraph = historyStack.pop();
			setGraph(restoredGraph);
		}
		return !historyStack.isEmpty();
	}

	/**
	 * Changes mode of the pipeline canvas.
	 *
	 * @param newMode 
	 */
	public void changeMode(String newMode) {
		canvasMode = newMode;
		getRpcProxy(PipelineCanvasClientRpc.class).setStageMode(newMode);
	}

	/**
	 * Returns if PipelineCanvas was modified since last save.
	 *
	 * @return Is modified?
	 *
	 */
	public boolean isModified() {
		return isModified;
	}

	/**
	 * Cancel unsaved changes.
	 */
	public void cancelChanges() {
		isModified = false;
	}

	@Override
	protected PipelineCanvasState getState() {
		return (PipelineCanvasState) super.getState();
	}

	/**
	 * Inform listeners, about supplied event.
	 * 
	 * @param event 
	 */
	protected void fireEvent(Event event) {
		Collection<Listener> ls = (Collection<Listener>) this.getListeners(com.vaadin.ui.Component.Event.class);
		for (Listener l : ls) {
			l.componentEvent(event);
		}
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
		LOG.debug("DPU mandatory fields check starting");
		for (Node node : graph.getNodes()) {
			DPUInstanceRecord dpu = node.getDpuInstance();
			//boolean isValid = checkDPUValidity(dpu);
			getRpcProxy(PipelineCanvasClientRpc.class).addNode(node.hashCode(), dpu.getName(), dpu.getDescription(), dpu.getType().name(), node.getPosition().getX(), node.getPosition().getY(), false);
		}
		LOG.debug("DPU mandatory fields check completed");
		EdgeCompiler edgeCompiler = new EdgeCompiler();
		boolean hadInvalidMappings = false;
		String message = "Pipeline contained invalid mapping(s). They were removed. List of removed mappings:\n";
		for (Edge edge : graph.getEdges()) {
			List<String> invalidMappings = edgeCompiler.update(edge, dpuExplorer.getOutputs(edge.getFrom().getDpuInstance()), dpuExplorer.getInputs(edge.getTo().getDpuInstance()));
			if (!invalidMappings.isEmpty()) {
				hadInvalidMappings = true;
				message += String.format("Edge from %s to %s: %s.\n", edge.getFrom().getDpuInstance().getName(), edge.getTo().getDpuInstance().getName(), invalidMappings.toString());
			}
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(edge.hashCode(), edge.getFrom().hashCode(), edge.getTo().hashCode(), edgeFormater.format(edge.getScript()));
		}
		if (hadInvalidMappings) {
			Notification.show("Invalid mappings found!", message, Notification.Type.WARNING_MESSAGE);
		}
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
	 * Shows detail of given {@link Edge} in new sub-window.
	 *
	 * @param edge {@link Edge} which detail should be showed.
	 */
	private void showEdgeDetail(final Edge edge) {
		EdgeDetail edgeDetailDialog = new EdgeDetail(edge, dpuExplorer, canvasMode.equals(PipelineEdit.STANDARD_MODE));
		edgeDetailDialog.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				isModified = true;
				fireEvent(new DetailClosedEvent(PipelineCanvas.this, Edge.class));
				getRpcProxy(PipelineCanvasClientRpc.class).updateEdge(edge.hashCode(), edgeFormater.format(edge.getScript()));
			}
		});
		UI.getCurrent().addWindow(edgeDetailDialog);
	}

	/**
	 * Store graph in stack for undo.
	 */
	private void storeHistoryGraph() {
		PipelineGraph clonedGraph = graph.cloneGraph();
		isModified = true;
		if (historyStack.isEmpty()) {
			//Make undo button enabled.
			fireEvent(new GraphChangedEvent(this, true));
		}

		historyStack.push(clonedGraph);
	}

	/**
	 * Copy DPURecord on canvas.
	 *
	 * @param nodeId Id of Node, which DPURecord should be copied.
	 *
	 */
	private void copyDpu(int nodeId, int x, int y) {
		storeHistoryGraph();
		Node node = graph.getNodeById(nodeId);

		DPUInstanceRecord dpu = new DPUInstanceRecord(node.getDpuInstance());
		Node copyNode = graph.addDpuInstance(dpu);
		graph.moveNode(copyNode.hashCode(), x, y);
		getRpcProxy(PipelineCanvasClientRpc.class)
				.addNode(copyNode.hashCode(), dpu.getName(), dpu.getDescription(), dpu.getType().name(), x, y, true);
	}

	/**
	 * Start pipeline in debug mode and show debug window.
	 *
	 * @param dpuId {@Link int} id of dpu, where debug should end.
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	private void showDebugWindow(int dpuId) throws IllegalArgumentException, NullPointerException {
		Node debugNode = graph.getNodeById(dpuId);
		fireEvent(new ShowDebugEvent(this, debugNode));
	}

	/**
	 * Validate graph.
	 */
	public void validateGraph() {
		boolean isGraphValid = true;
		for (Node node : graph.getNodes()) {
			DPUInstanceRecord dpu = node.getDpuInstance();
			boolean isValid = pipelineValidator.checkDPUValidity(dpu);
			isGraphValid &= isValid;
			getRpcProxy(PipelineCanvasClientRpc.class).setDpuValidity(node.hashCode(), isValid);
		}
		try {
			isGraphValid &= pipelineValidator.validateGraphEdges(graph);
			if (isGraphValid) {
				Notification.show("Pipeline is valid!", Notification.Type.WARNING_MESSAGE);
			}
		} catch (PipelineValidationException ex) {
			Notification.show("Mandatory input/output(s) missing!", ex.getMessage(), Notification.Type.WARNING_MESSAGE);
		}
	}

	/**
	 * Invoke formatting action.
	 *
	 * @param action Formatting action.
	 */
	public void formatAction(String action) {
		getRpcProxy(PipelineCanvasClientRpc.class).formatDPUs(action);
	}
}
