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
package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import java.util.Set;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;

/**
 * Manage context for a single execution.
 * 
 * @author Petyr
 */
public class ExecutionInfo {

    /**
     * Binded context.
     */
    private final ExecutionContextInfo executionContext;

    /**
     * Create manager class for given execution context.
     * 
     * @param executionContext
     *            Context for which create {@link ExecutionInfo}.
     */
    public ExecutionInfo(ExecutionContextInfo executionContext) {
        this.executionContext = executionContext;
    }

    /**
     * If the context for given DPU has already been created then return it,
     * otherwise return null.
     * 
     * @param dpuInstance
     *            Respective dpu.
     * @return Information about given DPU context or null.
     */
    public DpuContextInfo dpu(DPUInstanceRecord dpuInstance) {
        if (executionContext.getContexts().containsKey(dpuInstance)) {
            return new DpuContextInfo(executionContext, dpuInstance);
        } else {
            return null;
        }
    }

    /**
     * @return DPU instances for which the execution context has been created.
     */
    public Set<DPUInstanceRecord> getDPUIndexes() {
        return executionContext.getContexts().keySet();
    }

    /**
     * Delete data from context info. The state remain unchanged.
     */
    public void clear() {
        executionContext.getContexts().clear();
    }


    public ExecutionContextInfo getExecutionContext() {
        return executionContext;
    }

}
