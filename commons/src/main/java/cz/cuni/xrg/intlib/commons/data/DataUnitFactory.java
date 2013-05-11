package cz.cuni.xrg.intlib.commons.data;

/**
 * Interface for DataUnit factory.
 * 
 * @author Petyr
 *
 */
public interface DataUnitFactory {	
	/**
	 * Return instance of class that implements required interface.
	 * @see @{link DataUnitType}
	 * @param type Required @{link DataUnitType} type.
	 * @return @{link DataUnit} or null in case of unsupported type.
	 */
	public DataUnit create(DataUnitType type);
}
