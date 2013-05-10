package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Contains support for:
 * 	identifying pipeline and dpu related to the context 
 * 
 * @author Petyr
 *
 */
public interface ExtendedContext {
	
	/**
	 * Return related pipeline execution.
	 * @return
	 */
	public PipelineExecution getPipelineExecution();
	
	/**
	 * Return owner of the context. 
	 * (The one who work with it.) 
	 * @return
	 */
	public DPUInstance getDPUInstance();

}
