package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.LinkedList;

import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import java.util.List;
import virtuoso.hibernate.VirtuosoDialect;

/**
 * Contains and manage information about execution for single {@link DPU}.
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "exec_context_dpu")
public class ProcessingUnitInfo {

	/**
	 * Unique id of pipeline execution.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Dummy column to work around virtuoso's missing support for no-column
	 * insert. To be removed in future if other persisted attributes are added.
	 * 
	 * @see VirtuosoDialect#getNoColumnsInsertString()
	 */
	@SuppressWarnings("unused")
	@Column
	private int dummy = 0;
	
	/**
	 * Storage for dataUnits descriptors.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "exec_context_dpu_id")
	@OrderBy("index")
	private List<DataUnitInfo> dataUnits = new LinkedList<>();
	
	public ProcessingUnitInfo() { }
	
	/**
	 * Create information about new DataUnit and return path to it's directory.
	 * The storage directory should be used to save data by method
	 * {@link cz.cuni.xrg.intlib.commons.data.DataUnit#save()} and load from by
	 * {@link cz.cuni.xrg.intlib.commons.data.DataUnit#load()}.
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
			index = dataUnits.get(dataUnits.size()-1).getIndex() + 1;
		}
		DataUnitInfo dataUnitInfo = new DataUnitInfo(index, name, type, isInput);
		dataUnits.add(dataUnitInfo);
		// return index
		return index;
	}
	
	/**
	 * Return DataUnit info with given index.
	 * @param index Index of DataUnit.
	 * @return DataUnitInfo or null.
	 */
	public DataUnitInfo getDataUnit(Integer index) {
		for(DataUnitInfo info : dataUnits) {
			if (info.getIndex() == index) {
				return info;
			}
		}
		return null;
	}
	
	public List<DataUnitInfo> getDataUnits() {
		return dataUnits;
	}
	
}
