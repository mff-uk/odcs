package cz.cuni.xrg.intlib.commons.loader;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;

/**
 * Is responsible for loading the RDF data.
 * 
 * 
 * @author Jiri Tomes
 */
public interface Load {

	/**
	 * Loads the RDF data of an completed extract and transform cycle to a data
	 * sink.<br/>
	 * For convenience and flexibility reasons the repository and the graph is
	 * provided, so that loaders can query only parts of the RDF data.<br/>
	 * <strong>Loaders may only read the RDF data and must not transform
	 * it!</strong>
	 * 
	 * @param context The context containing meta information about this load
	 *            process
	 * @throws LoadException If loading fails, this exception has to be thrown
	 * @throws DataUnitException
	 * @throws DPUException
	 */
	public void load(LoadContext context)
			throws LoadException,
				DataUnitException,
				DPUException;
}
