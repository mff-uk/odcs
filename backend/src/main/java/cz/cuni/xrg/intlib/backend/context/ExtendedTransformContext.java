package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Extended transform context.
 * 
 * @author Petyr
 *
 */
public interface ExtendedTransformContext extends cz.cuni.xrg.intlib.commons.transformer.TransformContext, ExtendedContext, MergableContext {
	
	/**
	 * Made inputs read only. It's called just before it's passed to the DPURecord.
	 */
	public void sealInputs();	
		
	/**
	 * Return access to list of all output DataUnits.
	 * @return
	 */
	public List<DataUnit> getOutputs();	
}
