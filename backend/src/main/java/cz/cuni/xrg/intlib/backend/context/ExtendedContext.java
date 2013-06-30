package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;

/**
 * Contains support for:
 * 	identifying pipeline and DPURecord related to the context 
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
	public DPUInstanceRecord getDPUInstance();
	
    /**
     * Release all locks from context and DataUnits. 
     * Prepare for being deleted.
     */
    public void release();	
    
    /**
     * Save all data units into their respective directories.
     */
    public void save();
}
