/**
 * This package should contains implementations of 
 * {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.PreExecutor}s 
 * and {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.PostExecutor}.
 * 
 * The package is auto-discovered for spring component. Use 
 * {@link org.springframework.stereotype.Component}
 * annotation to automatically connect processor to the execution. 
 * 
 * The {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl.PostExecutorBase}
 * and {@link cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl.PreExecutorBase}
 * offers base implementation that enable running post/pre executor
 * code only for given {@link cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState}. 
 * 
 * @author Petyr
 *
 */
package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;