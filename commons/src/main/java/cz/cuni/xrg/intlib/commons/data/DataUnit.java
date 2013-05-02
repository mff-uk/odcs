package cz.cuni.xrg.intlib.commons.data;

/**
 * Basic data unit interface. The data unit should be passed in context 
 * between modules and should carry the main information.
 * 
 * @author Petyr
 *
 */
public interface DataUnit {
	
	/**
	 * Return type of data unit interface implementation.
	 * @return
	 */
	public DataUnitType getType();
}
