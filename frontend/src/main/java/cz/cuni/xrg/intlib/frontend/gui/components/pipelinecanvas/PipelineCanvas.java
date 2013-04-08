package cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas;


import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;


/**
 * Component for visualization of the pipeline.
 * @author Bogo
 */
@SuppressWarnings("serial")
@JavaScript({ "js_pipelinecanvas.js", "kinetic-v4.3.3.min.js" })
public class PipelineCanvas extends AbstractJavaScriptComponent {

	int dpuCount = 0;
	int connCount = 0;

	private Pipeline pipeline;

	private int CONNECTION_SEED = 1000;

	public int getCONNECTION_SEED() {
		return CONNECTION_SEED;
	}

	public PipelineCanvas() {

		//TODO: Factory method for pipeline?
		this.pipeline = new Pipeline();

		this.setId("container");
		//this.setWidth(1500,  Unit.PIXELS);
		//this.setHeight(800, Unit.PIXELS);
		this.setStyleName("pipelineContainer");

		registerRpc(new PipelineCanvasServerRpc() {

			@Override
			public void onDetailRequested(int dpuId) {
				// TODO Auto-generated method stub
				// propably publish event one level higher
			}

			@Override
			public void onConnectionRemoved(int connectionId) {
				pipeline.RemoveEdge(connectionId);
			}

			@Override
			public void onConnectionAdded(int dpuFrom, int dpuTo) {
				addConnection(dpuFrom, dpuTo);
			}

			@Override
			public void onDpuRemoved(int dpuId) {
				pipeline.RemoveDpu(dpuId);

			}
		});
	}

	public void init() {
        getRpcProxy(PipelineCanvasClientRpc.class).init();
    }

	public void addDpu(DPU dpu) {
		int dpuInstanceId = pipeline.AddDpu(dpu);
		getRpcProxy(PipelineCanvasClientRpc.class).addNode(dpuInstanceId, dpu.getName(), dpu.getDescription(), -5, -5);
	}

	public void addConnection(int dpuFrom, int dpuTo) {
		int connectionId = pipeline.AddEdge(dpuFrom, dpuTo);
		getRpcProxy(PipelineCanvasClientRpc.class).addEdge(connectionId, dpuFrom, dpuTo);
	}

	//OR loadPipeline
	public void showPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
		for(Node node : pipeline.getGraph().getNodes()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addNode(node.getId(), node.getDpuInstance().getName(), node.getDpuInstance().getDescription(), node.getPosition().getX(), node.getPosition().getY());
		}
		for(Edge edge : pipeline.getGraph().getEdges()) {
			getRpcProxy(PipelineCanvasClientRpc.class).addEdge(edge.getId(), edge.getFrom().getId(), edge.getTo().getId());
		}
	}

	public Pipeline getPipeline() {
//		for(DpuInstance dpu : pipeline.getDpus()) {
//			int[] position = getRpcProxy(PipelineCanvasClientRpc.class).getDpuPosition(dpu.Id);
//			dpu.setX(position[0]);
//			dpu.setY(position[1]);
//		}
		//pipeline.setWidth(Math.round(this.getWidth()));
		//pipeline.setHeight(Math.round(this.getHeight()));

		return pipeline;
	}

	@Override
	  protected PipelineCanvasState getState() {
	    return (PipelineCanvasState) super.getState();
	  }
}
