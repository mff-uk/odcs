package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.ProcessingContext;

/**
 * Extended load context.
 * 
 * @author Petyr
 *
 */
public interface LoadContext extends cz.cuni.xrg.intlib.commons.loader.LoadContext, ExtendedContext {
	
	/**
	 * Add information from given context to the actual context.
	 * Can be called multiple times with different contexts.
	 * If context can be added or error occur throws.
	 * 
	 * @param context Source context, do not change!
	 * @throws ContextException
	 */
	public void addSource(ProcessingContext context) throws ContextException;
	
}
