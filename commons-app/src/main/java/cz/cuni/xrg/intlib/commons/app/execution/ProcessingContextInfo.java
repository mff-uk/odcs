package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;

// TODO Honza: Add to database, this class is used in ExecutionContextImpl

/**
 * Store information about context of single DPURecord. 
 * Is used as a data container in class {@link cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextImpl}
 * 
 * @author Petyr
 *
 */
class ProcessingContextInfo {
	
	/**
	 * Storage for dataUnits descriptors.
	 */
	private HashMap<Integer, DataUnitInfo> dataUnits = new HashMap<>();
	
	/**
	 * Path to the storage directory or null if the directory
	 * has't been used yet.
	 */
	private File storageDirectory = null;
	
	/**
	 * Path to the result storage directory or null if the directory
	 * has't been used yet.
	 */		
	private File resultDirectory = null;
	
	/**
	 * DPURecord's root working directory.
	 */
	private File rootDirectory = null;
	
	/**
	 * Empty ctor because of JAXB.
	 */
	public ProcessingContextInfo() { }
	
	/**
	 * 
	 * @param rootDirectory DPURecord's root directory doesn't have to exit.
	 */
	public ProcessingContextInfo(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
		
	/**
	 * Return set of indexes for DataUnits.
	 * @return
	 */
	public Set<Integer> getIndexForDataUnits() {
		return dataUnits.keySet();
	}	
	
	/**
	 * Return {@link DataUnitInfo} for {@link DataUnit}. 
	 * @param index Index of {@link DataUnit}.
	 * @return {@link DataUnitType} or null in case of invalid id.
	 */
	public DataUnitInfo getDataUnitInfo(int index) {
		if (dataUnits.containsKey(index)) {
			return dataUnits.get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Return path to the directory for given input DataUnit. Is not
	 * secured that the returned directory exist.
	 * @param type DataUnit type.
	 * @param index Index of input DataUnit.
	 * @return The directory or null.
	 */
	public File createInputDir(DataUnitType type, int index) {
		if (dataUnits.containsKey(index)) {
			return dataUnits.get(index).getDirectory();
		} else {
			File path = new File(rootDirectory, Integer.toString(index) );
			dataUnits.put(index, new DataUnitInfo(path, type, true));
			return path;
		}
	}
	
	/**
	 * Return path to the directory for given output DataUnit. Is not
	 * secured that the returned directory exist.
	 * @param type DataUnit type.
	 * @param index Index of output DataUnit.
	 * @return The directory or null.
	 */	
	public File createOutputDir(DataUnitType type, int index) {
		if (dataUnits.containsKey(index)) {
			return dataUnits.get(index).getDirectory();
		} else {
			File path = new File(rootDirectory, Integer.toString(index) );
			dataUnits.put(index, new DataUnitInfo(path, type, false));
			return path;
		}
	}	
	
	/**
	 * Return the storage directory or this associated DPURecord.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File getDirForDPUStorage() {
		if (storageDirectory == null ){
			storageDirectory = new File(rootDirectory, "storage");
		}
		return storageDirectory;
	}
	
	/**
	 * Return the result directory or this associated DPURecord.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File getDirForDPUResult(boolean add) {
		if (resultDirectory == null ){
			resultDirectory = new File(rootDirectory, "result");
		}
		return resultDirectory;
	}		

}