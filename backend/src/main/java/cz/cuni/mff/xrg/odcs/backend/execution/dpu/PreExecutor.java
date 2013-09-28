package cz.cuni.mff.xrg.odcs.backend.execution.dpu;

import java.util.Map;

import org.springframework.core.Ordered;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Provide action that should be perform before every DPU execution. Must not
 * execute the DPU. T
 * 
 * The {@link PreExecutor}s are used as a singletons, so they
 * must be able to run concurrently on multiple instances.
 * 
 * The PreExecutors are executed in order that is defined by
 * {@link Ordered}
 * 
 * @author Petyr
 * 
 */
public interface PreExecutor extends Ordered {
	
	/**
	 * Should perform pre-execution actions. If return false then the execution
	 * is cancelled. In such case it should publish event
	 * {@link DPUPreExecutorFailed} with problem description.
	 * 
	 * @param node Node that will be executed.
	 * @param contexts List of context, also contain context for current node.
	 * @param dpuInstace DPU instance.
	 * @param execution Respective execution.
	 * @param unitInfo DPU's ProcessingUnitInfo.
	 * @return
	 */
	public boolean preAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo);
	
}
