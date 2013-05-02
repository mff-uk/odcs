package cz.cuni.xrg.intlib.commons.loader;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;

import java.util.List;

/**
 * Context used by {@link Load}s for the loading process.
 *
 * @see Load
 * @author Petyr
 */
public interface LoadContext extends ProcessingContext {

	/**
	 * Return list of input data units.
	 * @return
	 */
	public List<DataUnit> getInputs();
		
}
