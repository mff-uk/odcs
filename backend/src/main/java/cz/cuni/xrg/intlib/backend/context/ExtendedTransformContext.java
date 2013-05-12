package cz.cuni.xrg.intlib.backend.context;

/**
 * Extended transform context.
 * 
 * @author Petyr
 *
 */
public interface ExtendedTransformContext extends cz.cuni.xrg.intlib.commons.transformer.TransformContext, ExtendedContext, MergableContext {
	
	/**
	 * Made inputs read only. It's called just before it's passed to the DPU.
	 */
	public void sealInputs();	
		
}
