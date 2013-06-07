package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

// TODO Honza: Add to database, see PipelineExecution

/**
 * Implementation of {@link ExecutionContext}. Manage resources during 
 * pipeline execution.
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "exec_context_pipeline")
class ExecutionContextImpl implements ExecutionContext {

	/**
	 * Unique id of pipeline execution.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	

	/**
	 * Contexts for DPU's. Indexed by id's of DPUINstanceRecord
	 */
    @OneToMany()
    @JoinTable(name="exec_context_proccontext")
    @MapKeyColumn(name="dpu_execution")
    private Map<Long, ProcessingContextInfo> contexts = new HashMap<>();
	
	/**
	 * Working directory for execution.
	 */
    @Column(name="directory")
	private File directory;
	
	/**
	 * Empty ctor for JPA. Do not use.
	 */
	public ExecutionContextImpl() { }
	
	public ExecutionContextImpl(File directory) {
		this.contexts = new HashMap<>();
		this.directory = directory;		
	}
	
	/**
	 * Return context for given DPUInstanceRecord. Create new context if need.
	 * @param id DPUInstanceRecord's id.
	 * @return ProcessingContextInfo
	 */
	private ProcessingContextInfo getContext(DPUInstanceRecord dpuInstance) {
		// check existence
		Long id = dpuInstance.getId();
		if (!contexts.containsKey(id)) {
			// add new 
			File root = new File(directory, Long.toString(id));
			contexts.put(id, new ProcessingContextInfo(root));
		}
		// return data
		return contexts.get(id);
	}
	
	@Override
	public boolean containsData(DPUInstanceRecord dpuInstance) {
		return contexts.containsKey(dpuInstance.getId());
	}

	@Override
	public Set<Integer> getIndexesForDataUnits(DPUInstanceRecord dpuInstance) {
		return getContext(dpuInstance).getIndexForDataUnits();
	}

	@Override
	public DataUnitInfo getDataUnitInfo(DPUInstanceRecord dpuInstance, int index) {
		return getContext(dpuInstance).getDataUnitInfo(index);
	}

	@Override
	public File getDirectoryForResult(DPUInstanceRecord dpuInstance) {
		return getContext(dpuInstance).getDirForDPUResult(false);
	}

	@Override
	public File createDirForInput(DPUInstanceRecord dpuInstance,
			DataUnitType type, int index) {
		return getContext(dpuInstance).createInputDir(type, index);
	}

	@Override
	public File createDirForOutput(DPUInstanceRecord dpuInstance,
			DataUnitType type, int index) {
		return getContext(dpuInstance).createOutputDir(type, index);
	}

	@Override
	public File createDirForDPUStorage(DPUInstanceRecord dpuInstance) {
		return getContext(dpuInstance).getDirForDPUStorage();
	}

	@Override
	public File createDirForDPUResult(DPUInstanceRecord dpuInstance) {
		return getContext(dpuInstance).getDirForDPUResult(true);
	}
	
	public File getWorkingDirectory() {
		return directory;
	}
}
