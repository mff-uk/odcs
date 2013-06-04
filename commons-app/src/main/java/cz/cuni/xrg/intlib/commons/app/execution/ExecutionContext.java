package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Complete read write interface for execution context. Enable writing
 * data into context and asking for directories.
 * 
 * @author Petyr
 *
 */
public interface ExecutionContext extends ExecutionContextReader {

	/**
	 * Get directory where the input {@link cz.cuni.xrg.intlib.commons.data.DataUnit} 
	 * of given id (index) could store it's content.
	 * @param dpuInstance The {@link DPUInstanceRecord} which will work with the DataUnit.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @param index DataUnit' index.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File createDirForInput(DPUInstanceRecord dpuInstance, DataUnitType type, int index);

	/**
	 * Get directory where the output {@link cz.cuni.xrg.intlib.commons.data.DataUnit} 
	 * of given id (index) could store it's content.
	 * @param dpuInstance The {@link DPUInstanceRecord} which will work with the DataUnit.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @param index DataUnit' index.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File createDirForOutput(DPUInstanceRecord dpuInstance, DataUnitType type, int index);	
	
	/**
	 * Create directory where DPU can store it's content 
	 * ie. function {@link cz.cuni.xrg.intlib.commons.ProcessingContext#storeData} and 
	 * {@link cz.cuni.xrg.intlib.commons.ProcessingContext#loadData}. 
	 * @param dpuInstance The {@link DPUInstanceRecord}.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File createDirForDPUStorage(DPUInstanceRecord dpuInstance);
	
	/**
	 * Create directory where DPURecord could store data that should be accessible to the 
	 * user after the pipeline run. Do not use this to store debug data.
	 * @param dpuInstance dpuInstance The {@link DPUInstanceRecord} for which crate the directory.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File createDirForDPUResult(DPUInstanceRecord dpuInstance);
}
