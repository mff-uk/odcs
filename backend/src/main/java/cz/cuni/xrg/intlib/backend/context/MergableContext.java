package cz.cuni.xrg.intlib.backend.context;

import cz.cuni.xrg.intlib.commons.ProcessingContext;

/**
 * Represent context, that be be merged with other context.
 * 
 * @author Petyr
 *
 */
public interface MergableContext {

	/**
	 * Add information from given context to the actual context.
	 * Can be called multiple times with different contexts.
	 * If context can't be added or error occur then throws.
	 * 
	 * @param context Source context, do not change!
	 * @param merger Class used to merge DataUnits.
	 * @throws ContextException
	 */
	public void addSource(ProcessingContext context, DataUnitMerger merger) throws ContextException;	
	
}
