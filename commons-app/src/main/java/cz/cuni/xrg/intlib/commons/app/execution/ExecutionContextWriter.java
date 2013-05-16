package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Writer interface for DPUContext. Enable writing
 * data into context and asking for directories.
 * 
 * @author Petyr
 *
 */
public interface ExecutionContextWriter {

	/**
	 * Get directory where the {@link cz.cuni.xrg.intlib.commons.data.DataUnit} 
	 * of given id (index) could store it's content.
	 * @param dpuInstance The {@link DPUInstance} which will work with the {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @param index DataUnit' index.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File createDirForDataUnit(DPUInstance dpuInstance, DataUnitType type, int index);

	/**
	 * Create directory where DPU {@link cz.cuni.xrg.intlib.commons.ProcessingContext} 
	 * can store it's content ie. function  {@link cz.cuni.xrg.intlib.commons.ProcessingContext#storeData} and 
	 * {@link cz.cuni.xrg.intlib.commons.ProcessingContext#loadData}. 
	 * @param dpuInstance The {@link DPUInstance} which will work with the DataUnit.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File getDirForDPUStorage(DPUInstance dpuInstance);
	
	/**
	 * Create directory where DPU could store data that should be accessible to the 
	 * user after the pipeline run. Do not use this to store debug data.
	 * @param dpuInstance dpuInstance The {@link DPUInstance} for which crate the directory.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File getDirForDPUResult(DPUInstance dpuInstance);
	
}
