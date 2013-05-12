package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * TODO Honza
 *
 */
public class PipelineExecutionFacade {

	// Dummy impl
	public List<PipelineExecution> getAllPipelines() {
		return new LinkedList<>();
	}
	
	// Dummy impl
	public PipelineExecution createPipelineExecution(Pipeline pipeline) {
		PipelineExecution execution = new PipelineExecution(pipeline);
		return execution;
	}

	// Dummy impl
	public void save(PipelineExecution pipeline) {
		
	}
}
	
