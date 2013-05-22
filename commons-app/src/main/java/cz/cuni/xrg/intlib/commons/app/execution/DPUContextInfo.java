package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Store information about context of single DPU. 
 * Is used as a data container in class {@link cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextImpl}
 * 
 * @author Petyr
 *
 */
class DPUContextInfo {
	
	/**
	 * Storage for dataUnits descriptors.
	 */
	@XmlElement
	private HashMap<Integer, DataUnitInfo> dataUnits = new HashMap<>();
	
	/**
	 * Path to the storage directory or null if the directory
	 * has't been used yet.
	 */
	@XmlElement
	private File storageDirectory = null;
	
	/**
	 * Path to the result storage directory or null if the directory
	 * has't been used yet.
	 */		
	@XmlElement
	private File resultDirectory = null;
	
	/**
	 * DPU's root working directory.
	 */
	@XmlElement
	private File rootDirectory = null;
	
	/**
	 * Empty ctor because of JAXB.
	 */
	public DPUContextInfo() {
		
	}
	
	/**
	 * 
	 * @param rootDirectory DPU's root directory doesn't have to exit.
	 */
	public DPUContextInfo(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	/**
	 * Return set of indexes for stored DataUnits.
	 * @return
	 */
	public Set<Integer> getIndexForDataUnits() {
		return dataUnits.keySet();
	}	
	
	/**
	 * Return {@link DataUnitInfo} for given {@link DataUnit}. 
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
	 * Return path to the directory associated with given. If the path 
	 * doesn't exist then it's created.
	 * @param type DataUnit type.
	 * @param isInput True it the DataUnit is created as a input.
	 * @param index Index of DataUnit.
	 * @return The directory or null.
	 */
	public File createDirForDataUnit(DataUnitType type, boolean isInput, int index) {
		if (dataUnits.containsKey(index)) {
			return dataUnits.get(index).getDirectory();
		} else {
			File path = new File(rootDirectory, Integer.toString(index) );
			dataUnits.put(index, new DataUnitInfo(path, isInput, type));
			return path;
		}
	}
	
	/**
	 * Return the storage directory or this associated DPU.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File getDirForDPUStorage() {
		if (storageDirectory == null ){
			storageDirectory = new File(rootDirectory, "storage");
		}
		return storageDirectory;
	}
	
	/**
	 * Return the result directory or this associated DPU.
	 * @return The path to the directory, is not guaranteed that the directory exist.
	 */
	public File getDirForDPUResult(boolean add) {
		if (resultDirectory == null ){
			resultDirectory = new File(rootDirectory, "result");
		}
		return resultDirectory;
	}		

}