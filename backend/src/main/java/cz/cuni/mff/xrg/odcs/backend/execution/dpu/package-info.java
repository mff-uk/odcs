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
/**
 * This package provide functionality for execution single DPU instance
 * ie. {@link cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord} attached
 * to {@link cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution}.
 * 
 * Provide possibility that enable simple extension in terms of custom
 * DPU's pre/post execution actions.
 *
 * The actual pre-processor chain is:
 * <ol>
 * <li>ContextPreparator</li>
 * <li>AnnotationsOutput</li>
 * <li>Restarter</li>
 * <li>AnnotationsInput</li>
 * <li>Configurator</li>
 * </ol> 
 * 
 * 
 * To locate new pre/Post processor relatively to existing processor
 * use processor static variable ORDER. The pre/post executors are executed
 * all no matter if some of them return false or not.
 * 
 * @author Petyr
 *
 */
package cz.cuni.mff.xrg.odcs.backend.execution.dpu;
