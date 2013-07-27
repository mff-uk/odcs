package cz.cuni.xrg.intlib.commons.context;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Add output functionality to the context. This enable to add
 * {@link DataUnit}s to the context as outputs.
 * 
 * The output {@link DataUnit}s can be used by other context as inputs. This
 * is the main way in which DPU can pass data to next DPU on pipeline.
 * 
 * @author Petyr
 * @see ContextInputs
 */
public interface ContextOutputs {

	/**
	 * Request creating a new output DataUnit of given type. If the requested
	 * {@link DataUnit} can't be created from any reason the 
	 * {@link DataUnitCreateException} is thrown.
	 * The DataUnit's name can be further change during DPU execution.
	 * 
	 * @param type Type of DataUnit.
	 * @param name DataUnit's name.
	 * @return Created DataUnit.
	 * @throw DataUnitCreateException
	 */
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException;

	/**
	 * Request creating a new output DataUnit of given type with given 
	 * configuration. If the requested
	 * {@link DataUnit} can't be created from any reason the 
	 * {@link DataUnitCreateException} is thrown.
	 * The DataUnit's name can be further change during DPU execution.
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
