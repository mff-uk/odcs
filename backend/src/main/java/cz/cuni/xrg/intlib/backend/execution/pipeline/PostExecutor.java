package cz.cuni.xrg.intlib.backend.execution.pipeline;

import org.springframework.core.Ordered;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;

/**
 * Provide action that should be perform after pipeline execution.
 * 
 * The PreExecutors are executed in order that is defined by
 * {@link Ordered}
 * 
 * @author Petyr
 *
 */
public interface PostExecutor extends Ordered {
	
	/**
	 * Should perform post-execution actions. If return false then the execution
	 * is cancelled.
	 * 
	 * @param execution
	 * @param graph Dependency graph used for execution.
	 * @return
	 */
	public boolean postAction(PipelineExecution execution, DependencyGraph graph);

}
