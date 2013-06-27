package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Contains and manage information about execution for single {@link DPU}.
 * 
 * @author Petyr
 *
 */
//@Entity
//@Table(name = "exec_context_dpu")
class ProcessingUnitInfo {

	/**
	 * Unique id of pipeline execution.
	 */
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	
	
	/**
	 * Storage for dataUnits descriptors.
	 */
    //@OneToMany()
    //@OrderBy("index")
	private LinkedList<DataUnitInfo> dataUnits = new LinkedList<>();
	
	public ProcessingUnitInfo() { }
	
	/**
	 * Create information about new DataUnit and return path to it's directory. 
	 * The storage directory should be used to save data by method {@link cz.cuni.xrg.intlib.commons.data.DataUnit#save()} 
	 * and load from by {@link cz.cuni.xrg.intlib.commons.data.DataUnit#load()}.
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
			index = dataUnits.getLast().getIndex() + 1;
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
	
	public LinkedList<DataUnitInfo> getDataUnits() {
		return dataUnits;
	}

}
