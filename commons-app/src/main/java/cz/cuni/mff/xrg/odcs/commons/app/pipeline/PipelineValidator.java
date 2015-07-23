/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.data.DataUnitDescription;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUExplorer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;

/**
 * Class for validating the pipelines.
 * 
 * @author Bogo
 * @author Petyr
 */
public class PipelineValidator {

    /**
     * Checks if all mandatory inputs and outputs of DPUs in given graph are
     * satisfied. Returns report with found problems or null for successful
     * check.
     * 
     * @param graph
     * @param explorer
     * @return report with found problems or null for success
     */
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
                    // TODO Do not use the -> directly
                    if (e.getScript().contains("-> " + input.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    report += "\n";
                    report += Messages.getString("PipelineValidator.dpu.input", dpu.getName(), input.getName());
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
                    // TODO Do not use the -> directly
                    if (e.getScript().contains(output.getName() + " ->")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    report += "\n";
                    report += Messages.getString("PipelineValidator.dpu.output", dpu.getName(), output.getName());
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
