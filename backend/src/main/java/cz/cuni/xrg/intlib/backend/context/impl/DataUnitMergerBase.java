package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions;

/**
 * Base class for DataUnit's merger. 
 * Provide functionality for working with {@link cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions}.
 * 
 * @author Petyr
 *
 */
class DataUnitMergerBase {

	/**
	 * Search for first command that can be applied to the DataUnit
	 * with given name.
	 * @param dataUnitName DataUnit's name.
	 * @param instruction
	 * @return Command or empty string.
	 */
	protected String findRule(String dataUnitName, String instruction) {
		// check for null
		if (instruction == null) {
			return "";
		}
		
		String [] rules = 
				instruction.split(DataUnitMergerInstructions.Separator.getValue());
		for (String item : rules) {
			String []elements = item.split(" ", 2);
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
	
}
