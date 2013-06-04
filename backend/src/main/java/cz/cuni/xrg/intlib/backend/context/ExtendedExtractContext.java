package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Extended extractor context.
 * 
 * @author Petyr
 *
 */
public interface ExtendedExtractContext extends cz.cuni.xrg.intlib.commons.extractor.ExtractContext, ExtendedContext {
	
	/**
	 * Return access to list of all output DataUnits.
	 * @return
	 */
	public List<DataUnit> getOutputs();
	
}
