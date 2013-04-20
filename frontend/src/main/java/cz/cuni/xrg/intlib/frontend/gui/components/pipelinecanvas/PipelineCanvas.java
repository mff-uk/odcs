package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;


import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.frontend.gui.components.DPUDetail;


/**
 * Component for visualization of the pipeline.
 * @author Bogo
 */
@SuppressWarnings("serial")
@JavaScript({ "js_pipelinecanvas.js", "kinetic-v4.4.3.min.js" })
public class PipelineCanvas extends AbstractJavaScriptComponent {

	int dpuCount = 0;
	int connCount = 0;

	private PipelineGraph graph;

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
                if(node != null) {
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
				graph.removeDpu(dpuId);

			}

			@Override
			public void onDpuMoved(int dpuId, int newX, int newY) {
				dpuMoved(dpuId, newX, newY);
			}

			@Override
			public void onLogMessage(String message) {
				//TODO: Log JS messages
			}
		});
	}

	public void init() {
        getRpcProxy(PipelineCanvasClientRpc.class).init();
    }

	private void dpuMoved(int dpuId, int newX, int newY) {
		graph.moveNode(dpuId, newX, newY);
	}

	public void addDpu(DPU dpu) {
		int dpuInstanceId = graph.addDpu(dpu);
		getRpcProxy(PipelineCanvasClientRpc.class).addNode(dpuInstanceId, dpu.getName(), dpu.getDescription(), -5, -5);
	}

	public void addConnection(int dpuFrom, int dpuTo) {
		int connectionId = graph.addEdge(dpuFrom, dpuTo);
		getRpcProxy(PipelineCanvasClientRpc.class).addEdge(connectionId, dpuFrom, dpuTo);
	}

	public void showPipeline(Pipeline pipeline) {
		this.graph = pipeline.getGraph();
		for(Node node : graph.getNodes()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addNode(node.getId(), node.getDpuInstance().getName(), node.getDpuInstance().getDescription(), node.getPosition().getX(), node.getPosition().getY());
		}
		for(Edge edge : graph.getEdges()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(edge.getId(), edge.getFrom().getId(), edge.getTo().getId());
		}
	}

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
				getRpcProxy(PipelineCanvasClientRpc.class).updateNode(node.getId(), dpu.getName(), dpu.getDescription());
			}
		});
        App.getApp().addWindow(detailDialog);

    }
}
