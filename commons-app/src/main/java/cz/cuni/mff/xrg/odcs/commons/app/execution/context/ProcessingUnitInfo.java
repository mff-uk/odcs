package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import java.util.LinkedList;

import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import java.io.Serializable;
import java.util.List;

/**
 * Contains and manage information about execution for single {@link DPU}. The
 * information class (this) is created at the start of the DPU execution. So the
 * information class in not accessible for all the DPUs from the beginning of the
 * execution.
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "exec_context_dpu")
public class ProcessingUnitInfo implements Serializable {

	/**
	 * Unique id of pipeline execution.
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_context_dpu")
	@SequenceGenerator(name = "seq_exec_context_dpu", allocationSize = 1)
	private Long id;

	/**
	 * Describe state of the DPU execution.
	 */
	@Enumerated(EnumType.ORDINAL)
	private DPUExecutionState state = DPUExecutionState.PREPROCESSING;

	/**
	 * Storage for dataUnits descriptors.
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "exec_context_dpu_id")
	@OrderBy("index")
	private List<DataUnitInfo> dataUnits = new LinkedList<>();

	/**
	 * Empty constructor for JPA.
	 */	
	public ProcessingUnitInfo() { }

	/**
	 * Create information about new DataUnit and return path to it's directory.
	 * The storage directory should be used to save data by method
	 * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit#save()} and load from by
	 * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit#load()}.
	 * 
	 * @param name
	 * @param type
	 * @param isInput
	 * @return Index of new DataUnitInfo.
	 */
	public Integer addDataUnit(String name, DataUnitType type, boolean isInput) {
		// add information
		Integer index = 0;
		if (dataUnits.isEmpty()) {
		} else {
			index = dataUnits.get(dataUnits.size() - 1).getIndex() + 1;
		}
		DataUnitInfo dataUnitInfo = new DataUnitInfo(index, name, type, isInput);
		dataUnits.add(dataUnitInfo);
		// return index
		return index;
	}

	/**
	 * Return DataUnit info with given index.
	 * 
	 * @param index Index of DataUnit.
	 * @return DataUnitInfo or null.
	 */
	public DataUnitInfo getDataUnit(Integer index) {
		for (DataUnitInfo info : dataUnits) {
			if (info.getIndex() == index) {
				return info;
			}
		}
		return null;
	}

	public List<DataUnitInfo> getDataUnits() {
		return dataUnits;
	}

	
	public DPUExecutionState getState() {
		return state;
	}
	

	public void setState(DPUExecutionState state) {
		this.state = state;
	}
}
