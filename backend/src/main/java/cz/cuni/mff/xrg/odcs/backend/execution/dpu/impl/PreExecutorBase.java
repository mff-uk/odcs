package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.util.List;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.execution.dpu.PreExecutor;
import cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Extended base implementation {@link PreExecutor}. Offers execution only for
 * given {@link DPUExecutionState}s.
 * 
 * Put the pre-executor code into 
 * {@link #execute(Node, Map, Object, PipelineExecution, ProcessingUnitInfo)}
 * method.
 * 
 * @author Petyr
 * 
 */
abstract class PreExecutorBase implements PreExecutor {

	/**
	 * Contains states on which this execution will be executed, other states
	 * are ignored.
	 */
	private final List<DPUExecutionState> executionStates;

	/**
	 * If true then execution only if the state of the DPU is in
	 * {@link executionStates} if false then executed otherwise.
	 */
	private final boolean polarity;

	/**
	 * @param executionStates List of {@link DPUExecutionState} on which run
	 *            {@link #execute(Node, Map, Object, PipelineExecution, ProcessingUnitInfo)}
	 */
	public PreExecutorBase(List<DPUExecutionState> executionStates) {
		this.executionStates = executionStates;
		this.polarity = true;
	}

	public PreExecutorBase(List<DPUExecutionState> executionStates,
			boolean polarity) {
		this.executionStates = executionStates;
		this.polarity = polarity;
	}

	@Override
	public boolean preAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo,
			boolean willExecute) {
		// shall we execute ?
		final boolean contains = executionStates.contains(unitInfo.getState());

		if ((contains && polarity) || (!contains && !polarity)) {
			return execute(node, contexts, dpuInstance, execution, unitInfo);
		} else {
			return true;
		}
	}

	/**
	 * Execute executor's code.
	 * @param node
	 * @param contexts
	 * @param dpuInstance
	 * @param execution
	 * @param unitInfo
	 * @return
	 */
	protected abstract boolean execute(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo);

}
