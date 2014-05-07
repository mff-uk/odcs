package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.execution.dpu.DPUPostExecutor;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Save the context after DPU execution.
 * @author Petyr
 */
@Component
public class CheckDataUnits implements DPUPostExecutor {

	public static final int ORDER = ContextSaver.ORDER + 1;

	@Override
	public int getOrder() {
		return ORDER;
	}	
	
	@Override
	public boolean postAction(Node node, Map<Node, Context> contexts, Object dpuInstance, PipelineExecution execution, ProcessingUnitInfo unitInfo) {
		// get the context
		Context context = contexts.get(node);
		// save it
		for (ManagableDataUnit managableDataUnit : context.getOutputs()) {
			managableDataUnit.isReleaseReady();
		}
		for (ManagableDataUnit managableDataUnit : context.getInputs()) {
			managableDataUnit.isReleaseReady();
		}
		// and return true
		return true;
	}
	
}
