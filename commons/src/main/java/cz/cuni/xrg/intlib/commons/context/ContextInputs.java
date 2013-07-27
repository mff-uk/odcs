package cz.cuni.xrg.intlib.commons.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Add input functionality to the context so the user of the context can get
 * access to {@link DataUnit}s, that are stored as inputs.  
 * 
 * The inputs are taken from other context where they were added as outputs.
 * 
 * @author Petyr
 * @see ContextOutputs
 */
public interface ContextInputs {
	
	/**
	 * Return list of input data units.
	 * @return
	 */
	public List<DataUnit> getInputs();
	
}
