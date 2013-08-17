package cz.cuni.xrg.intlib.backend.context;

import java.util.Iterator;
import java.util.List;

import cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;

/**
 * Basic interface for data merger.
 * 
 * @author Petyr
 * 
 */
class DataUnitMerger {

	/**
	 * Search for first command that can be applied to the DataUnit with given
	 * name.
	 * 
	 * @param dataUnitName DataUnit's name.
	 * @param instruction
	 * @return Command or empty string.
	 */
	protected String findRule(String dataUnitName, String instruction) {
		// check for null
		if (instruction == null) {
			return "";
		}

		String[] rules = instruction.split(DataUnitMergerInstructions.Separator
				.getValue());
		for (String item : rules) {
			String[] elements = item.split(" ", 2);
			// test name ..
			if (elements.length < 2) {
				// not enough data .. skip
			} else { // elements.length == 2
				if (elements[0].compareToIgnoreCase(dataUnitName) == 0) {
					// math !!
					return elements[1];
				}
			}
		}
		return "";
	}	
	
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
	public void merger(DataUnitManager left, List<DataUnit> right, 
			String instruction) throws ContextException {
		Iterator<DataUnit> iterRight = right.iterator();

		// add the rest from right
		while (iterRight.hasNext()) {
			
			DataUnit rightDataUnit = iterRight.next();
			String rightDataUnitName = rightDataUnit.getName();
			// name for new DataUnit, use right's name as default
			String leftDataUnitName = rightDataUnitName;
			// get command
			String cmd = this.findRule(rightDataUnitName, instruction);
			if (cmd == "") {
				// nothing .. use name from the rightDataUnit 
				leftDataUnitName = rightDataUnitName;
			} else {
				String[] cmdSplit = cmd.split(" ");
				if (cmdSplit[0].compareToIgnoreCase(
						DataUnitMergerInstructions.Rename.getValue()) == 0) {
					// renaming .. we need second arg
					if (cmdSplit.length == 2) {
						leftDataUnitName = cmdSplit[1];
					} else {
						// not enough parameters .. use right name
						leftDataUnitName = rightDataUnitName;
					}
				} else if (cmdSplit[0].compareToIgnoreCase(
						DataUnitMergerInstructions.Drop.getValue()) == 0) {
					// drop this DataUnit -> skip 
					continue;
				}
			
			}
			
			// create new data unit (in context into which we merge)
			DataUnit newDataUnit;
			try {
				newDataUnit = left.addDataUnit(rightDataUnit.getType(), leftDataUnitName);
			} catch (DataUnitCreateException e) {
				throw new ContextException("Failed to create input object.", e);
			}

			// and copy the data
			try {
				newDataUnit.merge(rightDataUnit);
			} catch (IllegalArgumentException e) {
				throw new ContextException(
						"Can't merge data units, type miss match.", e);
			} catch (Exception e) {
				throw new ContextException("Can't merge data units.", e);
			}
		}
	}

}
