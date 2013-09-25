package cz.cuni.xrg.intlib.backend.execution.pipeline;

import java.util.Map;

import org.springframework.core.Ordered;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.execution.dpu.Executor;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;

/**
 * Provide action that should be perform before starts the the pipeline
 * execution. Must not execute the DPU. The PreExecutors can be called in random
 * order, but they all will be called before the call of {@link Executor}.
 * 
 * The PreExecutors are executed in order that is defined by {@link Ordered}
 * 
 * @author Petyr
 * 
 */
public interface PreExecutor extends Ordered {

	/**
	 * Should perform pre-execution actions. If return false then the execution
	 * is cancelled. In such case it should publish event {@link PipelineEvent}
	 * with problem description.
	 * 
	 * @param execution
	 * @param contexts
	 * @param graph Dependency graph used for execution.
	 * @return
	 */
	public boolean preAction(PipelineExecution execution,
			Map<Node, Context> contexts,
			DependencyGraph graph);

}