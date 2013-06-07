package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;

// TODO Honza: Add to database, this class is used in ExecutionContextImpl

/**
 * Store information about context of single DPURecord. 
 * Is used as a data container in class {@link cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextImpl}
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "processingContextInfo")
class ProcessingContextInfo {
	
	/**
	 * Unique id of pipeline execution.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	
	
	/**
	 * Storage for dataUnits descriptors.
	 */
    @OneToMany()
    @OrderBy("id")
	private List<DataUnitInfo> dataUnits = new LinkedList<>();
	
	/**
	 * Path to the storage directory or null if the directory
	 * has't been used yet.
	 */
	@Column
	private File storageDirectory = null;
	
	/**
	 * Path to the result storage directory or null if the directory
	 * has't been used yet.
	 */
	@Column
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
	 * @deprecated Use getDataUnits instead.
	 */
	@Deprecated
	public Set<Integer> getIndexForDataUnits() {
		Set<Integer> res = new HashSet<Integer>();
		for (DataUnitInfo dataUnit : dataUnits) {
			res.add(dataUnit.getIndex());
		}		
		return res;
	}	
	
	/**
	 * Return {@link DataUnitInfo} for {@link DataUnit}. 
	 * @param index Index of {@link DataUnit}.
	 * @return {@link DataUnitType} or null in case of invalid id.
	 * @deprecated Use getDataUnits instead.
	 */
	@Deprecated
	public DataUnitInfo getDataUnitInfo(int index) {
		for (DataUnitInfo dataUnit : dataUnits) {
			if (dataUnit.getIndex() == index) {
				return dataUnit;
			}
		}
		return null;
	}
		
	/**
	 * Return path to the directory for given input DataUnit. Is not
	 * secured that the returned directory exist.
	 * @param type DataUnit type.
	 * @param index Index of input DataUnit.
	 * @return The directory or null.
	 */
	public File createInputDir(DataUnitType type, int index) {
		return createDataUnit(type, index, true);
	}
	
	/**
	 * Return path to the directory for given output DataUnit. Is not
	 * secured that the returned directory exist.
	 * @param type DataUnit type.
	 * @param index Index of output DataUnit.
	 * @return The directory or null.
	 */	
	public File createOutputDir(DataUnitType type, int index) {
		return createDataUnit(type, index, false);
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
	
	/**
	 * Return path to the directory for given DataUnit. Is not
	 * secured that the returned directory exist.
	 * @param type DataUnit type.
	 * @param index Index of output DataUnit.
	 * @param isInput True if DataUnit as input.
	 * @return The directory or null.
	 */	
	private File createDataUnit(DataUnitType type, int index, boolean isInput) {
		DataUnitInfo dataUnit = null;
		// try to get existing
		dataUnit = getDataUnitInfo(index);
		if (dataUnit == null) {
			// create new
			File path = new File(rootDirectory, Integer.toString(index) );
			dataUnit = new DataUnitInfo(path, type, isInput, index);
			dataUnits.add(dataUnit);
		}
		return dataUnit.getDirectory();		
	}

	/**
	 * Return list of stored dataUnits.
	 * @return Stored DataUnits.
	 */
	public List<DataUnitInfo> getDataUnits() {
		return dataUnits;
	}	
}