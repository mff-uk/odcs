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
//@Entity
//@Table(name = "exec_context_pipeline")
public class ExecutionContextInfo {

	/**
	 * Name of working sub directory.
	 */
	private static final String WORKING_DIR = "working";
	
	/**
	 * Name of DPU tmp directory.
	 */
	private static final String WORKING_TMP_DIR = "tmp";
	
	/**
	 * Name of storage directory in which the DataUnits are save into.
	 */
	private static final String STORAGE_DIR = "storage";
	
	/**
	 * Directory for results.
	 */
	private static final String RESULT_DIR = "result";
	
	/**
	 * Prefix for dpu folder.
	 */
	private static final String DPU_ID_PREFIX = "dpu_";
	
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
			// unknown context -> add
			contexts.put(id, new ProcessingUnitInfo());
		}
		// return data
		return contexts.get(id);
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
	public Integer createOutput(DPUInstanceRecord dpuInstance, String name, DataUnitType type)  {
		return getContext(dpuInstance).addDataUnit(name, type, false);
	}
	
	/**
	 * Return tmp directory for given DPU instance.
	 * Does not create the directory!
	 * @param dpuInstance
	 * @return
	 * @throws UnknownDPUInstanceException 
	 */
	public File getTmp(DPUInstanceRecord dpuInstance) {
		// secure DPU record existence
		getContext(dpuInstance);
		//
		File tmpDir = new File(rootDirectory, 
				WORKING_DIR + File.separatorChar + 
				DPU_ID_PREFIX + dpuInstance.getId().toString() + 
				File.separatorChar + WORKING_TMP_DIR);
		return tmpDir;
	}
	
	/**
	 * Return tmp directory (working) for DataUnit.
	 * Does not create the directory!
	 * @param dpuInstance Owner of DataUnit.
	 * @param index DataUnit index from 
	 * {@link #createInput(DPUInstanceRecord, String, DataUnitType)} or 
	 * {@link #createOutput(DPUInstanceRecord, String, DataUnitType)}
	 * @return
	 */
	public File getDataUnitTmp(DPUInstanceRecord dpuInstance, Integer index) {
		// secure DPU record existence
		getContext(dpuInstance);
		//		
		File tmpDir = new File(rootDirectory, 
				WORKING_DIR + File.separatorChar + 
				DPU_ID_PREFIX + dpuInstance.getId().toString() + 
				File.separatorChar + index.toString());
		return tmpDir;
	}
	
	/**
	 * Return storage directory for DataUnit, here should DataUnit store it's results 
	 * for possible further processing. Does not create the directory!
	 * @param dpuInstance Owner of DataUnit.
	 * @param index DataUnit index from 
	 * {@link #createInput(DPUInstanceRecord, String, DataUnitType)} or 
	 * {@link #createOutput(DPUInstanceRecord, String, DataUnitType)}
	 * @return
	 */
	public File getDataUnitStorage(DPUInstanceRecord dpuInstance, Integer index) {
		// secure DPU record existence
		getContext(dpuInstance);
		//		
		File storageDir = new File(rootDirectory, 
				STORAGE_DIR + File.separatorChar + 
				DPU_ID_PREFIX + dpuInstance.getId().toString() + 
				File.separatorChar + index.toString());
		return storageDir;		
	}
	
	/**
	 * Return directory where should DPUInstance store it's results that will 
	 * be available to the user. Does not create the directory!
	 * @param dpuInstance
	 * @return
	 */
	public File getResult(DPUInstanceRecord dpuInstance) {
		// secure DPU record existence
		getContext(dpuInstance);
		//		
		File storageDir = new File(rootDirectory, 
				RESULT_DIR + File.separatorChar + 
				DPU_ID_PREFIX+ dpuInstance.getId().toString() );
		return storageDir;			
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
	 * Return DataUnitInfo for DataUnit with given index.
	 * @param dpuInstance
	 * @param index
	 * @return DataUnitInfo or null of DataUnitInfo can't be found.
	 */
	public DataUnitInfo getDataUnitInfo(DPUInstanceRecord dpuInstance, Integer index) {
		if (contexts.containsKey(dpuInstance.getId())) {
			return contexts.get(dpuInstance.getId()).getDataUnit(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Return directory where all DPU's can store their results.
	 * @return
	 */
	public File getDirectoryForResults() {
		return new File(rootDirectory, RESULT_DIR);
	}

	/**
	 * Return path to the working directory.
	 * @return
	 */
	public File getWorkingDirectory() {
		File workDir = new File(rootDirectory, WORKING_DIR);
		return workDir;
	}
	
}
