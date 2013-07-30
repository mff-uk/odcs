package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 * Is responsible for extracting data from data source and convert it to RDF
 * data.
 * 
 * @author Jiri Tomes
 */
public interface Extract {

	/**
	 * Extracts data from a data source and converts it to RDF.<br/>
	 * 
	 * @param context Context for one extraction cycle containing meta
	 *            information about the extraction.	 * 
	 * @throws ExtractException If any error occurs throughout the extraction
	 *             cycle.
	 * @throws DataUnitException
	 * @throws DataUnitCreateException If failed to create a DataUnit.
	 */
	public void extract(ExtractContext context)
			throws ExtractException,
				DataUnitException,
				DataUnitCreateException;
}
