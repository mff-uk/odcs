package cz.cuni.xrg.intlib.commons.module.data;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 * Used to report missing input DataUnit.
 * 
 * @author Petyr
 *
 */
public class MissingInputException extends DataUnitException {

	public MissingInputException() {
		super("Missing input.");
	}	
	
	public MissingInputException(String message) {
		super(message);
	}

}
