package cz.cuni.xrg.intlib.backend.execution.dpu;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Provide action that should be perform before every DPU execution. Must not 
 * execute the DPU. The PreExecutors can be called in random order, but
 * they all will be called before the call of {@link Executor}.
 * 
 * 
 * @author Petyr
 *
 */
public interface PreExecutor {

	/**
	 * Should perform pre-execution actions. If return false then the execution
	 * is cancelled. In such case it should publish event 
	 * {@link DPUPreExecutorFailed} with problem description.
	 * 
	 * @param dpu Respective DPU.
	 * @param dpuInstace DPU instance.
	 * @param execution Respective execution.
	 * @param context DPU's context.
	 * @return
	 */
	public boolean preAction(DPUInstanceRecord dpu,
			Object dpuInstance,
			PipelineExecution execution,
			Context context);	
	
}
