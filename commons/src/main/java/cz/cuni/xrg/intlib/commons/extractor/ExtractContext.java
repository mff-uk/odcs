package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.context.ContextOutputs;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;

/**
 * Context used by {@link Extract}s for the extraction process.
 * The extractor has no inputs as it extract it's data from outer sources 
 * outside the scope of context. Extracted data are stored to 
 * context as outputs.
 * 
 * @author Petyr
 * @see Extract
 */
public interface ExtractContext extends ProcessingContext, ContextOutputs {

}
