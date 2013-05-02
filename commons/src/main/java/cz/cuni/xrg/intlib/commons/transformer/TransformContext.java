package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;

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
	 * Return list of output data units.
	 * @return
	 */
	public List<DataUnit> getOutputs();
	
	/**
	 * Add data unit to output data list.
	 * @param dataUnit
	 */
	public void addOutputDataUnit(DataUnit dataUnit);
}