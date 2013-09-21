package cz.cuni.xrg.intlib.backend.execution.dpu;

import java.util.Map;

import org.springframework.core.Ordered;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;

/**
 * Provide action that should be perform after every DPU execution.
 * 
 * The PreExecutors are executed in order that is defined by
 * {@link Ordered#getOrder()}
 * 
 * @author Petyr
 *
 */
public interface PostExecutor extends Ordered {

	/**
	 * Should perform post-execution actions. If return false then the execution
	 * is cancelled.
	 * 
	 * @param dpu Respective DPU.
	 * @param execution Respective execution.
	 * @param context DPU's context.
	 * @param unitInfo DPU's ProcessingUnitInfo.
	 * @return
	 */
	public boolean postAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo);

}
