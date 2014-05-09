package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information.
 *
 * Each DataUnit has URI, this can can't be changed by DPU directly. It's
 * assigned once when DataUnit is created. The URI can be obtained using
 * {@link #getDataUnitName()}
 *
 * @author Petyr
 *
 */
public interface DataUnit {

	/**
	 * Return type of data unit interface implementation.
	 *
	 * @return DataUnit type.
	 */
	public DataUnitType getType();
	
	/**
	 * Check my type against provided.
	 * 
	 * @return True if equals
	 */
	boolean isType(DataUnitType dataUnitType);

	/**
	 * Return dataUnit's URI. The DataUnit URI should be set in constructor.
	 * Otherwise it should be immutable.
	 *
	 * @return String name of data unit.
	 */
	public String getDataUnitName();
	
    /**
     * Add all data from given DataUnit into this DataUnit.
     * The method must not modify the current parameter (unit).
     *
     * @param unit {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} to add from
     * @throws IllegalArgumentException if some property of an element of the
     *         specified dataunit prevents it from being added to this
     *         dataunit
     */
	void addAll(DataUnit unit);  

}
