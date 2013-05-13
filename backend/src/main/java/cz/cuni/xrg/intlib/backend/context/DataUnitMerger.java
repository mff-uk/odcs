package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;

/**
 * Basic interface for data merger.
 * 
 * @author Petyr
 *
 */
public interface DataUnitMerger {

	/**
	 * Merge the data, the result is store in 'left'. If 
	 * the two Lists of DataUnits can't be merge throw ContextException.
	 * 
	 * @param left
	 * @param right
	 * @param DataUnitFactory Factory used to create new DataUnits.
	 * @throw ContextException
	 */
	public void merger(List<DataUnit> left, List<DataUnit> right, DataUnitFactory factory) throws ContextException;
	
}
