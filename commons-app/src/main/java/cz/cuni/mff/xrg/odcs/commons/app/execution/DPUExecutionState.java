/**
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
 */
package cz.cuni.mff.xrg.odcs.commons.app.execution;

/**
 * Describe states for DPU execution.
 * 
 * @see cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo
 * @author Petyr
 */
public enum DPUExecutionState {
    /**
     * The DPU is in pre-processing state. It's
     * the default state. Only DPU with this state can be executed.
     */
    PREPROCESSING,
    /**
     * The DPU is currently being executed. This state is from start
     * of DPU execution method. The post-processing is not part
     * of this state.
     */
    RUNNING,
    /**
     * The DPU execution has been finished.
     */
    FINISHED,
    /**
     * DPU execution failed.
     */
    FAILED,
    /**
     * DPU execution has been aborted on user request.
     */
    ABORTED
}
