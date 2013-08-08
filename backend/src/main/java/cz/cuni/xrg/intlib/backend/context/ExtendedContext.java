package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 * Contains support for: 
 * <ul> 
 * <li>identifying pipeline and DPURecord related to the context</li> 
 * <li>saving/loading context</li>
 * </ul>
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
     * Release all locks from context and DataUnits do not delete data.
     */
    public void release();

    /**
     * Release all lock from context and DataUnits. Also delete all stored
     * {@link DataUnit}s and related contex's directories.
     */
    public void delete();
    
    /**
     * Save all data units.
     */
    public void save();
    
    /**
     * Reload DataUnit's if they are not loaded.
     * @throws DataUnitCreateException
     */
    public void reload() throws DataUnitException;
}
