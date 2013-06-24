package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Hold and manage context for pipeline execution.
 * 
 * Complete read write interface for execution context. Enable writing
 * data into context and asking for directories. Provide methods
 * for creating file names for DataUnits.
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "exec_context_pipeline")
public class ExecutionContextInfo {

	/**
	 * Unique id of pipeline execution.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	
	
	/**
	 * Root directory for execution.
	 */
    @Column(name="directory")
	private File rootDirectory;    
    
	/**
	 * Contexts for DPU's. Indexed by id's of DPUInstanceRecord.
	 */
    @OneToMany()
    @JoinTable(name="exec_context_proccontext")
    @MapKeyColumn(name="dpu_execution")
    private Map<Long, ProcessingUnitInfo> contexts = new HashMap<>();    

    /**
     * Empty ctor for JPA.
     */
    public ExecutionContextInfo() {}
    
    /**
     * 
     * @param directory Path to the root directory for execution.
     */
    public ExecutionContextInfo(File directory) {
    	rootDirectory = directory;
    }
    
	/**
	 * Return context for given DPUInstanceRecord. Create new context if need.
	 * @param id DPUInstanceRecord's id.
	 * @return DataProcessingUnitInfo
	 */
	private ProcessingUnitInfo getContext(DPUInstanceRecord dpuInstance) {
		// check existence
		Long id = dpuInstance.getId();
		if (!contexts.containsKey(id)) {
			// add new 
			contexts.put(id, new ProcessingUnitInfo());
		}
		// return data
		return contexts.get(id);
	}    
    
	/**
	 * Return path to the root directory for given dpuInstance.
	 * @param dpuInstance
	 * @return
	 */
	private File getPathToDpuDirectory(DPUInstanceRecord dpuInstance) {
		return new File(rootDirectory, dpuInstance.getId().toString() );
	}
	
	/**
	 * Add info record for new input {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * @param dpuInstance The {@link DPUInstanceRecord} which will work with the DataUnit.
	 * @param name Name of data unit.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @return Index of new DataUnitInfo.
	 */
	public Integer createInput(DPUInstanceRecord dpuInstance, String name, DataUnitType type) {
		return getContext(dpuInstance).addDataUnit(name, type, true);
	}
    
	/**
	 * Add info record for new output {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * @param dpuInstance The {@link DPUInstanceRecord} which will work with the DataUnit.
	 * @param name Name of data unit.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @return Index of new DataUnitInfo.
	 */
	public Integer createOutput(DPUInstanceRecord dpuInstance, String name, DataUnitType type) {
		return getContext(dpuInstance).addDataUnit(name, type, false);
	}
	
	/**
	 * Return path to the directory where could {@link cz.cuni.xrg.intlib.commons.data.DataUnit}
	 * store it's content. It's not working directory.
	 * @param dpuInstance
	 * @param index
	 * @return Null if no context for given dpuInstance exist or if index is invalid.
	 */
	public File getDataUnitStorage(DPUInstanceRecord dpuInstance, Integer index) {
		if (contexts.containsKey(dpuInstance.getId())) {
			ProcessingUnitInfo unitInfo = contexts.get(dpuInstance.getId());
			if (unitInfo.getDataUnit(index) == null) {
				// no DataUnit for this index
				return null;
			}			
			// create path
			File relativeDpu = getPathToDpuDirectory(dpuInstance);			
			return new File(relativeDpu, index.toString());
			
		} else {
			return null;
		}
	}
	
	/**
	 * Create directory where DPU can store it's content 
	 * ie. function {@link cz.cuni.xrg.intlib.commons.ProcessingContext#storeData} and 
	 * {@link cz.cuni.xrg.intlib.commons.ProcessingContext#loadData}. 
	 * @param dpuInstance The {@link DPUInstanceRecord}.
	 * @return The path to the directory.
	 */
	public File createDirForDPUStorage(DPUInstanceRecord dpuInstance) {
		File result = new File(getPathToDpuDirectory(dpuInstance), "Storage" );
		result.mkdirs();
		return result;
	}
	
	/**
	 * Create directory where DPURecord could store data that should be accessible to the 
	 * user after the pipeline run. Do not use this to store debug data.
	 * @param dpuInstance dpuInstance The {@link DPUInstanceRecord} for which crate the directory.
	 * @return The path to the directory.
	 */
	public File createDirForDPUResult(DPUInstanceRecord dpuInstance) {
		// secure existence of record
		getContext(dpuInstance);
		// get root directory
		File rootResultDir = new File(rootDirectory, "Storage");
		// get directory for given instance		
		File result = new File(rootResultDir, dpuInstance.getId().toString());
		result.mkdirs();
		return result;
	}
	
	/**
	 * Return root workingDirectory.
	 * @return workingDirecotry.
	 */
	public File getWorkingDirectory() {
		return rootDirectory;
	}
	
	/**
	 * Return true if the context has some data about
	 * execution of certain {@link DPUInstance}
	 * @param dpuInstance
	 * @return 
	 */	
	public boolean containsData(DPUInstanceRecord dpuInstance) {
		return contexts.containsKey(dpuInstance.getId());
	}	
	
	/**
	 * Return list of DataUnitInfo for DPUInstanceRecord that can be used in {@link #getInputInfo}.
	 * @param dpuInstance Instance of DPU for which DataUnit retrieve DataUnit's indexes.
	 * @return Set of indexes or null if there is no data for given dpuInstance.
	 */	
	public LinkedList<DataUnitInfo> getDataUnitsInfo(DPUInstanceRecord dpuInstance) {
		if (contexts.containsKey(dpuInstance.getId())) {
			return contexts.get(dpuInstance.getId()).getDataUnits();
		} else {
			return null;
		}
	}
	
	/**
	 * Return directory where the result from given DPURecord are be stored.
	 * @param dpuInstance The author of results.
	 * @return Null in case of bad dpuInstance.
	 */
	public File getDirectoryForResult(DPUInstanceRecord dpuInstance) {
		if (contexts.containsKey(dpuInstance.getId())) {			
			// already exist won't be created again
			return createDirForDPUResult(dpuInstance);			
		} else {
			return null;
		}
	}
	
	/**
	 * Return directory where all DPU's have their results directories.
	 * @return
	 */
	public File getDirectoryForResults() {
		return new File(rootDirectory, "Storage");
	}

	/**
	 * Return working directory.
	 * @return
	 */
	public File getWorkingRootDirectory() {
		return new File(rootDirectory, "Working");
	}

}
