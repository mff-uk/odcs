package cz.cuni.xrg.intlib.backend.execution.dpu.impl;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.DPUExecutionState;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;

/**
 * Reset the DPU state from {@link DPUExecutionState#RUNNING} 
 * to {@link DPUExecutionState#PREPROCESSING}.
 * 
 * Executed only for {@link DPUExecutionState#RUNNING}.
 * 
 * @author Petyr
 *
 */
@Component
public class Restarter extends PreExecutorBase {

	public static final int ORDER = AnnotationsOutput.ORDER + 1000;
	
	private static final Logger LOG = LoggerFactory
			.getLogger(Restarter.class);
	
	public Restarter() {
		super(Arrays.asList(DPUExecutionState.RUNNING));
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	protected boolean execute(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo) {
		LOG.info("Restarting DPU from RUNNING -> PREPROCESSING");
		// TODO Petyr: delete data here
		unitInfo.setState(DPUExecutionState.PREPROCESSING);
		
		return true;
	}

}
