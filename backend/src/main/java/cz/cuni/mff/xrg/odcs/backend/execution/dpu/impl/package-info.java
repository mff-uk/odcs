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
/**
 * This package should contains implementations of 
 * {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.DPUPreExecutor}s 
 * and {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.DPUPostExecutor}.
 * 
 * The package is auto-discovered for spring component. Use 
 * {@link org.springframework.stereotype.Component}
 * annotation to automatically connect processor to the execution. 
 * 
 * The {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl.DPUPostExecutorBase}
 * and {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl.DPUPreExecutorBase}
 * offers base implementation that enable running post/pre executor
 * code only for given {@link cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState}. 
 * 
 * @author Petyr
 *
 */
package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;
