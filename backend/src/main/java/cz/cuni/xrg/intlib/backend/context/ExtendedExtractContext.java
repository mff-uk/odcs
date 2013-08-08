package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;

/**
 * Extended extractor context.
 * 
 * @author Petyr
 * 
 */
public interface ExtendedExtractContext extends ExtractContext, ExtendedContext {

	/**
	 * Return access to list of all output DataUnits.
	 * 
	 * @return
	 */
	public List<DataUnit> getOutputs();

}
