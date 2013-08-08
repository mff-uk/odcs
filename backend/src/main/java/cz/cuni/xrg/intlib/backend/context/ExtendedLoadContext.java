package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.loader.LoadContext;

/**
 * Extended load context.
 * 
 * @author Petyr
 * 
 */
public interface ExtendedLoadContext
		extends LoadContext, ExtendedContext, MergableContext {

	/**
	 * Made inputs read only. It's called just before it's passed to the
	 * DPURecord.
	 */
	public void sealInputs();

}
