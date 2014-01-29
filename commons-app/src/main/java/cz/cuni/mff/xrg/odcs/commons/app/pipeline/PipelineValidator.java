package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.data.DataUnitDescription;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUExplorer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import java.util.List;

/**
 * Class for validating the pipelines.
 *
 * @author Bogo 
 * @author Petyr
 */
public class PipelineValidator {

	public String checkMandatoryInputsAndOutputs(PipelineGraph graph, DPUExplorer explorer) {
		String report = "";
		for (Node node : graph.getNodes()) {
			DPUInstanceRecord dpu = node.getDpuInstance();
			List<Edge> edgesTo = null;
			List<DataUnitDescription> inputs = explorer.getInputs(dpu);
			if (!inputs.isEmpty()) {
				edgesTo = graph.getEdgesTo(node);
			}
			for (DataUnitDescription input : inputs) {
				boolean found = false;
				if (input.getOptional()) {
					continue;
				}
				for (Edge e : edgesTo) {
					if (e.getScript().contains("-> " + input.getName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					report += String.format("\nDPU: %s, Input: %s", dpu.getName(), input.getName());
				}
			}
			List<DataUnitDescription> outputs = explorer.getOutputs(dpu);
			List<Edge> edgesFrom = null;
			if (!outputs.isEmpty()) {
				edgesFrom = graph.getEdgesFrom(node);
			}
			for (DataUnitDescription output : outputs) {
				boolean found = false;
				if (output.getOptional()) {
					continue;
				}
				for (Edge e : edgesFrom) {
					if (e.getScript().contains(output.getName() + " ->")) {
						found = true;
						break;
					}
				}
				if (!found) {
					report += String.format("\nDPU: %s, Output: %s", dpu.getName(), output.getName());
				}
			}
		}
		if (report.isEmpty()) {
			return null;
		} else {
			return report;
		}
	}

}
