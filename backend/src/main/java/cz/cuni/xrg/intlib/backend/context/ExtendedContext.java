package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Contains support for:
 * 	identifying pipeline and DPURecord related to the context 
 *  saving/loading context
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
     */
    public void release();	

    /**
     * Release all lock from context and DataUnits. Also delete all stored
     * {@link DataUnit}s.
     */
    public void delete();
    
    /**
     * Save all data units into their respective directories.
     */
    public void save();
}
