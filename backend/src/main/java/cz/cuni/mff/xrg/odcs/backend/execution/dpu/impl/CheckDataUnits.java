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
package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.execution.dpu.DPUPostExecutor;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Check the state of data units.
 *  
 */
@Component
public class CheckDataUnits implements DPUPostExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(CheckDataUnits.class);

    public static final int ORDER = ContextSaver.ORDER + 1;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public boolean postAction(Node node, Map<Node, Context> contexts, Object dpuInstance, PipelineExecution execution, ProcessingUnitInfo unitInfo) {
        final Context context = contexts.get(node);
        boolean result = true;
        for (ManagableDataUnit managableDataUnit : context.getOutputs()) {
            try {
                managableDataUnit.checkConsistency();
            } catch (DataUnitException ex) {
                LOG.error("dataUnit.checkConsistency failed.", ex);
                result = false;
            }
        }
        for (ManagableDataUnit managableDataUnit : context.getInputs()) {
            try {
                managableDataUnit.checkConsistency();
            } catch (DataUnitException ex) {
                LOG.error("dataUnit.checkConsistency failed.", ex);
                result = false;
            }
        }
        return result;
    }

}
