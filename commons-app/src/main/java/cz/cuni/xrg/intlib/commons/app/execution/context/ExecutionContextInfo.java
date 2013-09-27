package cz.cuni.xrg.intlib.commons.app.execution.context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import java.io.Serializable;

/**
 * Hold and manage context for pipeline execution.
 * 
 * Complete read write interface for execution context. Enable writing data into
 * context and asking for directories. Provide methods for creating file names
 * for DataUnits.
 * 
 * The directory structure used by context is following;
 * ./working/DPU_ID/DATAUNIT_INDEX/ - DataUnit working directory
 * ./working/DPU_ID/tmp/ - DPU working directory
 * ./storage/DPU_ID/DATAUNIT_INDEX/ - storage for DataUnit results ./result/ -
 * place for DPU's files that should be accessible to the user
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "exec_context_pipeline")
public class ExecutionContextInfo implements Serializable {

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
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_context_pipeline")
	private Long id;

	/**
	 * Id of respective execution. Used to create relative path to the context
	 * directory.
	 */
	@OneToOne(mappedBy = "context")
	private PipelineExecution execution;

	/**
	 * Dummy column, because Virtuoso cannot insert a row without specifying any
	 * column values. Remove when entity has an attribute without default value.
	 */
	@SuppressWarnings("unused")
	private Boolean dummy = false;

	/**
	 * Contexts for DPU's. Indexed by {@link DPUInstanceRecord}.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@MapKeyJoinColumn(name = "dpu_instance_id", referencedColumnName = "id")
	@JoinColumn(name = "exec_context_pipeline_id")
	private Map<DPUInstanceRecord, ProcessingUnitInfo> contexts;

	/**
	 * Empty constructor for JPA.
	 */
	public ExecutionContextInfo() {
		contexts = new HashMap<>();
	}

	/**
	 * 
	 * @param directory Path to the root directory for execution.
	 */
	public ExecutionContextInfo(PipelineExecution execution) {
		this.contexts = new HashMap<>();
		this.execution = execution;
	}

	/**
	 * Return context for given DPUInstanceRecord. Create new context if need.
	 * 
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
	 * Add info record for new input
	 * {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * 
	 * @param dpuInstance The {@link DPUInstanceRecord} which will work with the
	 *            DataUnit.
	 * @param name Name of data unit.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @return Index of new DataUnitInfo.
	 */
	public Integer createInput(DPUInstanceRecord dpuInstance,
			String name,
			DataUnitType type) {
		return getContext(dpuInstance).addDataUnit(name, type, true);
	}

	/**
	 * Add info record for new output
	 * {@link cz.cuni.xrg.intlib.commons.data.DataUnit}.
	 * 
	 * @param dpuInstance The {@link DPUInstanceRecord} which will work with the
	 *            DataUnit.
	 * @param name Name of data unit.
	 * @param type {@link DataUnitType Type} of data unit.
	 * @return Index of new DataUnitInfo.
	 */
	public Integer createOutput(DPUInstanceRecord dpuInstance,
			String name,
			DataUnitType type) {
		return getContext(dpuInstance).addDataUnit(name, type, false);
	}

	/**
	 * Delete all data about execution except {@link #rootDirectory} and
	 * {@link #id} Use to start execution from the very beginning.
	 */
	public void reset() {
		contexts.clear();
	}

	/**
	 * Generate unique id for given DataUnit. If call multiple times for the
	 * same dpuInstance and DataUnit's index it return the same id. The id has
	 * following format: exec_{exec_id}_dpu_{dpu_id}_du_{du_id}.
	 * 
	 * @param dpuInstance Owner of the DataUnit.
	 * @param index DataUnit's index assigned to the DataUnit by context.
	 * @return Unique id.
	 */
	public String generateDataUnitId(DPUInstanceRecord dpuInstance,
			Integer index) {
		return "exec_" + execution.getId().toString() + "_dpu_"
				+ dpuInstance.getId().toString() + "_du_" + index.toString();
	}

	/**
	 * Return context information class {@link ProcessingUnitInfo} for given
	 * DPU. If the context does not exist, then create new.
	 * 
	 * @param dpuInstance Instance of DPU for which retrieve context info.
	 * @return {@link ProcessingUnitInfo}
	 */
	public ProcessingUnitInfo createDPUInfo(DPUInstanceRecord dpuInstance) {
		return getContext(dpuInstance);
	}

	/**
	 * Return set of indexes of stored DPU's execution information.
	 * 
	 * @return
	 */
	public Set<DPUInstanceRecord> getDPUIndexes() {
		return contexts.keySet();
	}

	public Long getId() {
		return id;
	}

	/**
	 * Return relative path from execution directory to the DPU's tmp directory.
	 * This directory will be deleted after the execution ends if not in debug
	 * mode. Does not create a directory!
	 * 
	 * @param dpuInstance The
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getDPUTmpPath(DPUInstanceRecord dpuInstance) {
		// secure DPU record existence
		getContext(dpuInstance);
		// ..
		return getWorkingPath() + File.separatorChar + DPU_ID_PREFIX
				+ dpuInstance.getId().toString() + File.separatorChar
				+ WORKING_TMP_DIR;
	}

	/**
	 * Return relative path from execution directory to the DPU DataUnit's root
	 * tmp directory. This directory will be deleted after execution ends if not
	 * in debug mode. Does not create a directory!
	 * 
	 * @param dpuInstance
	 * @param index DataUnitInfo index.
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getDataUnitRootStoragePath(DPUInstanceRecord dpuInstance) {
		// secure DPU record existence
		getContext(dpuInstance);		
		// ..
		return getWorkingPath() + File.separatorChar + DPU_ID_PREFIX
				+ dpuInstance.getId().toString();
	}

	/**
	 * Return relative path from execution directory to the DPU DataUnit's tmp
	 * directory. This directory will be deleted after execution ends if not in
	 * debug mode. Does not create a directory!
	 * 
	 * @param dpuInstance
	 * @param index DataUnitInfo index.
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getDataUnitTmpPath(DPUInstanceRecord dpuInstance,
			Integer index) {
		return getDataUnitRootStoragePath(dpuInstance) + File.separatorChar
				+ index.toString();
	}

	/**
	 * Return relative path from execution directory to the DPU DataUnit's root
	 * storage directory. The storage directory can be used to store DataUnits
	 * results. This directory will be deleted after execution ends if not in
	 * debug mode. Does not create a directory!
	 * 
	 * @param dpuInstance
	 * @param index DataUnitInfo index.
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getDataUnitRootTmpPath(DPUInstanceRecord dpuInstance) {
		// secure DPU record existence
		getContext(dpuInstance);
		// ..
		return getStoragePath() + File.separatorChar + DPU_ID_PREFIX
				+ dpuInstance.getId().toString();
	}

	/**
	 * Return relative path from execution directory to the DPU DataUnit's
	 * storage directory. The storage directory can be used to store DataUnits
	 * results. This directory will be deleted after execution ends if not in
	 * debug mode. Does not create a directory!
	 * 
	 * @param dpuInstance
	 * @param index DataUnitInfo index.
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getDataUnitStoragePath(DPUInstanceRecord dpuInstance,
			Integer index) {
		return getDataUnitRootTmpPath(dpuInstance) + File.separatorChar
				+ index.toString();
	}

	/**
	 * Return context information class {@link ProcessingUnitInfo} for given
	 * DPU.
	 * 
	 * @param dpuInstance Instance of DPU for which retrieve context info.
	 * @return {@link ProcessingUnitInfo} or null if no records for given
	 *         dpuInstance exist.
	 */
	public ProcessingUnitInfo getDPUInfo(DPUInstanceRecord dpuInstance) {
		if (contexts.containsKey(dpuInstance)) {
			return contexts.get(dpuInstance);
		} else {
			return null;
		}
	}

	/**
	 * Return relative path from execution directory to the execution working
	 * directory.
	 * 
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getWorkingPath() {
		return getRootPath() + File.separatorChar + WORKING_DIR;
	}

	/**
	 * Return relative path from execution directory to the execution result
	 * directory.
	 * 
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getResultPath() {
		return File.separatorChar + RESULT_DIR;
	}

	/**
	 * Return relative path from execution directory to the execution data unit
	 * storage directory.
	 * 
	 * @return Relative path, start but not end with separator (/, \\)
	 */
	public String getStoragePath() {
		return File.separatorChar + STORAGE_DIR;
	}

	/**
	 * Return relative path from execution directory to the execution root
	 * directory.
	 * 
	 * @return Relative path start but not end with separator separator (/, \\).
	 */
	public String getRootPath() {
		return File.separatorChar + execution.getId().toString();
	}

	/**
	 * Return respective pipeline execution.
	 * @return
	 */
	public PipelineExecution getExecution() {
		return this.execution;
	}
	
}
