package cz.cuni.xrg.intlib.backend.data;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Container for associating DataUnits with indexes. 
 * 
 * @author Petyr
 *
 */
public class DataUnitContainer {

	/**
	 * Stored data unit.
	 */
	private DataUnit dataUnit;
	
	/**
	 * Associated index.
	 */
	private Integer index;
	
	public DataUnitContainer(DataUnit dataUnit, Integer index) {
		this.dataUnit = dataUnit;
		this.index = index;
	}

	public DataUnit getDataUnit() {
		return dataUnit;
	}

	public Integer getIndex() {
		return index;
	}
}
