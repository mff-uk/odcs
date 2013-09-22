/**
 * This package should contains implementations of 
 * {@link cz.cuni.xrg.intlib.backend.execution.dpu.PreExecutor}s 
 * and {@link cz.cuni.xrg.intlib.backend.execution.dpu.PostExecutor}.
 * 
 * The package is auto-discovered for spring component. Use {@link Component}
 * annotation to automatically connect processor to the execution. 
 * 
 * The {@link cz.cuni.xrg.intlib.backend.execution.dpu.impl.PostExecutorBase}
 * and {@link cz.cuni.xrg.intlib.backend.execution.dpu.impl.PreExecutorBase}
 * offers base implementation that enable running post/pre executor
 * code only for given {@link cz.cuni.xrg.intlib.commons.app.execution.DPUExecutionState}. 
 * 
 * @author Petyr
 *
 */
package cz.cuni.xrg.intlib.backend.execution.dpu.impl;