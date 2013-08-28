package cz.cuni.xrg.intlib.backend.execution.dpu;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Provide action that should be perform after every DPU execution.
 * 
 * @author Petyr
 *
 */
public interface PostExecutor {

	/**
	 * Should perform post-execution actions. If return false then the execution
	 * is cancelled.
	 * 
	 * @param dpu Respective DPU.
	 * @param execution Respective execution.
	 * @param context DPU's context.
	 * @return
	 */
	public boolean postAction(DPUInstanceRecord dpu,
			PipelineExecution execution,
			Context context);

}
