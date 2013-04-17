package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

/**
 * Information about executed pipeline and their states.
 *
 * @author Jiri Tomes
 */
public class PipelineExecution implements Runnable {

    /**
     * Actual status for executed pipeline.
     */
    private ExecutionStatus status;
    
    /**
     * Pipeline for executing.
     */
    private Pipeline pipeline;
    
    private ModuleFacade modules;

    public PipelineExecution(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus newStatus) {
        status = newStatus;
    }
    
    public void setModuleFacade(ModuleFacade modules) {
    	this.modules = modules;
    }

	/**
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * @param pipeline the pipeline to set
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}
    
    public void run() {
    	
    	DependencyGraph dGraph = new DependencyGraph(pipeline.getGraph());
    	for (Node node : dGraph) {
    		DPUInstance iDpu = node.getDpuInstance();
    		DPU dpu = iDpu.getDpu();
    		String uri = dpu.getJarPath();
    		
    		switch (dpu.getType()) {
    			case EXTRACTOR :
    				runExtractor(modules.getInstanceExtract(uri));
    				break;
    			case TRANSFORMER :
    				runTransformer(modules.getInstanceTransform(uri));
    				break;
    			case LOADER :
    				runLoader(modules.getInstanceLoader(uri));
    				break;
    			default :
    				throw new RuntimeException("Unknown DPU type.");
    		}
    	}
    }
    
    private void runExtractor(Extract dpu) {

    }
    
    private void runTransformer(Transform dpu) {
    	
    }
    
    private void runLoader(Load dpu) {
    
    }
    
}
