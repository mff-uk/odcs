package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information.
 * Each DataUnit has URI, this can can't be changed by DPU directly. It's
 * assigned once when DataUnit is created. The URI can be obtained using {@link #getDataUnitName()}
 * 
 * @author Petyr
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
}
