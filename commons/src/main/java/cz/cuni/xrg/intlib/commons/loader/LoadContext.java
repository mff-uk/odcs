package cz.cuni.xrg.intlib.commons.loader;

import cz.cuni.xrg.intlib.commons.context.ContextInputs;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;

/**
 * Context used by {@link Load}s for the loading process.
 * The load context has no outputs as Loader store it's information outside 
 * the scope of context.
 *
 * @author Petyr
 * @see Load
 */
@Deprecated
public interface LoadContext extends ProcessingContext, ContextInputs { 
	
}
