package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Basic interface for data merger.
 * 
 * @author Petyr
 * 
 */
public interface DataUnitMerger {

	/**
	 * Merge the data, the result is store in 'left'. If the two Lists of
	 * DataUnits can't be merge throw ContextException.
	 * 
	 * @param left Target {@link DataUnitManager}.
	 * @param right Source of DataUnits, do not change!
	 * @param instruction Instruction for merger. See
	 *            {@link cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions}
	 * @throw ContextException
	 */
	public void merger(DataUnitManager left,
			List<DataUnit> right,
			String instruction) throws ContextException;

}
