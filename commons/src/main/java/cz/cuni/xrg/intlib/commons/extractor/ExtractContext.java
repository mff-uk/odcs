package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;

import java.util.List;

/**
 * Context used by {@link Extract}s for the extraction process.
 *
 * @see Extract
 * @author Petyr
 */
public interface ExtractContext extends ProcessingContext {

	/**
	 * Return list of output data units.
	 * @return
	 */
	public List<DataUnit> getOutput();
	
	/**
	 * Add data unit to output data list.
	 * @param dataUnit
	 */
	public void addOutputDataUnit(DataUnit dataUnit);
}
