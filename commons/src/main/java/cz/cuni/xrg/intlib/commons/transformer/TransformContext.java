package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

import java.util.List;

/**
 * Context used by {@link Transform}s for the transformation process.
 *
 * @see Transform
 * @author Petyr
 */
public interface TransformContext extends ProcessingContext {

	/**
	 * Return list of input data units.
	 * @return
	 */
	public List<DataUnit> getInputs();	

	/**
	 * Request creating a new output DataUnit of given type.
	 * @param type Type of DataUnit.
	 * @return Created DataUnit.
	 */
	public DataUnit addOutputDataUnit(DataUnitType type) throws DataUnitCreateException;
	
	/**
	 * Request creating a new output DataUnit of given type.
	 * @param type Type of DataUnit.
	 * @param configu DataUnit initial configuration object.
	 * @return Created DataUnit.
	 */
	public DataUnit addOutputDataUnit(DataUnitType type, Object configu) throws DataUnitCreateException;	
}