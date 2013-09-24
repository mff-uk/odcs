package cz.cuni.xrg.intlib.backend.execution.dpu;

import java.util.Map;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;

/**
 * Provide action that should be perform before every DPU execution. Must not
 * execute the DPU. The PreExecutors can be called in random order, but they all
 * will be called before the call of {@link Executor}.
 * 
 * The PreExecutors are executed in order that is defined by
 * {@link Ordered#getOrder()}
 * 
 * @author Petyr
 * 
 */
public interface PreExecutor {

	/**
	 * Return the order value of this object, with a
	 * higher value meaning greater in terms of sorting in pre executors
	 * chain.
	 * 
	 * @return
	 */
	public int getPreExecutorOrder();	
	
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
