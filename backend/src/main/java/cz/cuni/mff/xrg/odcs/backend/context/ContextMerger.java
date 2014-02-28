package cz.cuni.mff.xrg.odcs.backend.context;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.data.EdgeInstructions;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Provide functionality to merge (add) one {@link Context} into another.
 * 
 * @author Petyr
 * 
 */
class ContextMerger {

	private static final Logger LOG = LoggerFactory
			.getLogger(ContextMerger.class);

	/**
	 * Add data from right {@link Context} into left {@link Context}.
	 * 
	 * @param left
	 * @param right
	 * @param instruction
	 *            Instructions that should be used for merging.
	 */
	public void merge(Context left, Context right, String instruction)
			throws ContextException {
		// merge dataUnits
		merger(left.getInputsManager(), right.getOutputs(), instruction);
	}

	/**
	 * Search for first command that can be applied to the DataUnit with given
	 * name.
	 * 
	 * @param dataUnitName
	 *            DataUnit's name.
	 * @param instruction
	 * @return Command or empty string.
	 */
	private String findRule(String dataUnitName, String instruction) {
		// check for null
		if (instruction == null) {
			return "";
		}

		String[] rules = instruction.split(EdgeInstructions.Separator
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
	 * @param left
	 *            Target {@link DataUnitManager}.
	 * @param right
	 *            Source of DataUnits, do not change!
	 * @param instruction
	 *            Instruction for merger. See
	 *            {@link cz.cuni.mff.xrg.odcs.commons.app.execution.DataUnitMergerInstructions}
	 * @throw ContextException
	 */
	private void merger(DataUnitManager left, List<ManagableDataUnit> right,
			String instruction) throws ContextException {
		Iterator<ManagableDataUnit> iterRight = right.iterator();

		// add the rest from right
		while (iterRight.hasNext()) {

			DataUnit rightDataUnit = iterRight.next();
			String rightDataUnitName = rightDataUnit.getDataUnitName();
			// name for new DataUnit, use right's name as default
			String leftDataUnitName = rightDataUnitName;
			// get command
			String cmd = this.findRule(rightDataUnitName, instruction);
			if (cmd.isEmpty()) {
				// there is no mapping
				// IGNORE DATAUNIT
				continue;
			} else {
				String[] cmdSplit = cmd.split(" ");
				if (cmdSplit[0]
						.compareToIgnoreCase(EdgeInstructions.Rename
								.getValue()) == 0) {
					// renaming .. we need second arg
					if (cmdSplit.length == 2) {
						leftDataUnitName = cmdSplit[1];
						LOG.debug("renaming: {} -> {}", rightDataUnitName,
								leftDataUnitName);
					} else {
						// not enough parameters .. use right name
						leftDataUnitName = rightDataUnitName;
						LOG.debug("passing: {}", rightDataUnitName);
					}
				} else {
					// unknown command
					LOG.error("dataUnit droped bacause of unknown command: {}",
							cmd);
					continue;
				}
			}

			// we need dataUnit into which merge data
			ManagableDataUnit leftDataUnit = null;
			// first check for existing one
			for (ManagableDataUnit item : left.getDataUnits()) {
				if (item.getDataUnitName().compareTo(leftDataUnitName) == 0
						&& item.getType() == rightDataUnit.getType()) {
					LOG.debug("merge into existing dataUnit: {}",
							rightDataUnitName);
					// DataUnit with same name and type already exist, use it
					leftDataUnit = item;
					break;
				}
			}

			// create new data unit (in context into which we merge)
			if (leftDataUnit == null) {
				try {
					LOG.debug("creating new dataUnit: {}", rightDataUnitName);
					leftDataUnit = left.addDataUnit(rightDataUnit.getType(),
							leftDataUnitName);
					// and clear it .. for sure that there is 
					// not data from previous executions
					leftDataUnit.clean();
				} catch (DataUnitCreateException e) {
					throw new ContextException(
							"Failed to create input object.", e);
				}
			}
			// and copy the data
			try {
				leftDataUnit.merge(rightDataUnit);
			} catch (IllegalArgumentException e) {
				throw new ContextException(
						"Can't merge data units, type miss match.", e);
			} catch (Throwable t) {
				throw new ContextException("Can't merge data units.", t);
			}
		}
	}

}
