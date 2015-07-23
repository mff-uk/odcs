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
