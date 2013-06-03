package cz.cuni.xrg.intlib.backend.context;

/**
 * Extended load context.
 * 
 * @author Petyr
 *
 */
public interface ExtendedLoadContext extends cz.cuni.xrg.intlib.commons.loader.LoadContext, ExtendedContext, MergableContext {
	
	/**
	 * Made inputs read only. It's called just before it's passed to the DPURecord.
	 */
	public void sealInputs();		
		
}
