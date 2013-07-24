package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Context used by {@link Extract}s for the extraction process.
 * 
 * @see Extract
 * @author Petyr
 */
public interface ExtractContext extends ProcessingContext {

	/**
	 * Request creating a new output DataUnit of given type.
	 * 
	 * @param type Type of DataUnit.
	 * @param name DataUnit's name.
	 * @return Created DataUnit.
	 * @throw DataUnitCreateException
	 */
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException;

	/**
	 * Request creating a new output DataUnit of given type.
	 * 
	 * @param type Type of DataUnit.
	 * @param name DataUnit's name.
	 * @param configu DataUnit initial configuration object.
	 * @return Created DataUnit.
	 * @throw DataUnitCreateException
	 */
	public DataUnit addOutputDataUnit(DataUnitType type,
			String name,
			Object config) throws DataUnitCreateException;
}
