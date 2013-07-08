package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import java.util.List;

/**
 * Hold and manage context for pipeline execution.
 * 
 * Complete read write interface for execution context. Enable writing
 * data into context and asking for directories. Provide methods
 * for creating file names for DataUnits.
 * 
 * The directory structure used by context is following; 
 * ./working/DPU_ID/DATAUNIT_INDEX/	- DataUnit working directory
 * ./working/DPU_ID/tmp/			- DPU working directory
 * ./storage/DPU_ID/DATAUNIT_INDEX/	- storage for DataUnit results
 * ./result/						- place for DPU's files that should be accessible to the user
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "exec_context_pipeline")
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
	 * Prefix for DPU folder.
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
	 * Every time path is changed, {@link #rootPath} needs to be updated, so
	 * that root directory is correctly persisted. Also with the same reasoning,
	 * this attribute needs to be immutable.
	 */
	@Transient
	private File rootDirectory;
	
	/**
	 * Helper attribute to be able to persist root directory as a path string.
	 * Must be synchronized with {@link #rootDirectory}.
	 */
    @Column(name="directory")
	private String rootPath;
    
	/**
	 * Contexts for DPU's. Indexed by {@link DPUInstanceRecord}.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@MapKeyJoinColumn(name = "dpu_instance_id", referencedColumnName = "id")
	@JoinColumn(name = "exec_context_pipeline_id")
	private Map<DPUInstanceRecord, ProcessingUnitInfo> contexts = new HashMap<>();

    /**
     * Empty constructor for JPA.
     */
    public ExecutionContextInfo() {}
    
    /**
     * 
     * @param directory Path to the root directory for execution.
     */
    public ExecutionContextInfo(File directory) {
    	rootDirectory = directory;
		rootPath = directory.getAbsolutePath();
    }
    
    
	/**
	 * Return context for given DPUInstanceRecord. Create new context if need.
	 * @param id DPUInstanceRecord's id.
	 * @return DataProcessingUnitInfo
	 */
	private ProcessingUnitInfo getContext(DPUInstanceRecord dpuInstance) {
		// check existence
		if (!contexts.containsKey(dpuInstance)) {
			// unknown context -> add
			ProcessingUnitInfo pui = new ProcessingUnitInfo();
			contexts.put(dpuInstance, pui);
		}
		// return data
		return contexts.get(dpuInstance);
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
	 * Delete all data about execution except {@link #rootDirectory}
 	 * and {@link #id} Use to start execution from the very beginning.
	 */
	public void reset() {
		contexts.clear();
	}
	
	/**
	 * Return set of indexes of stored DPU's execution information.
	 * @return
	 */
	public Set<DPUInstanceRecord> getDPUIndexes() {
		return contexts.keySet();
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
		return contexts.containsKey(dpuInstance);
	}	
	
	/**
	 * Return list of DataUnitInfo for DPUInstanceRecord that can be used in {@link #getInputInfo}.
	 * @param dpuInstance Instance of DPU for which DataUnit retrieve DataUnit's indexes.
	 * @return Set of indexes or null if there is no data for given dpuInstance.
	 */
	public List<DataUnitInfo> getDataUnitsInfo(DPUInstanceRecord dpuInstance) {
		if (contexts.containsKey(dpuInstance)) {
			return contexts.get(dpuInstance).getDataUnits();
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
		if (contexts.containsKey(dpuInstance)) {
			return contexts.get(dpuInstance).getDataUnit(index);
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
	
	/**
	 * Return path to the root directory.
	 * 
	 * @return defensively copied file for root directory.
	 */
	public File getRootDirectory() {
		return new File(rootDirectory.getAbsolutePath());
	}
}
