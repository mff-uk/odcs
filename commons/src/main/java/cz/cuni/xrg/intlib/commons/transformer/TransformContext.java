package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.context.ContextInputs;
import cz.cuni.xrg.intlib.commons.context.ContextOutputs;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;

/**
 * Context used by {@link Transform}s for the transformation process.
 * Transformer has inputs as well as outputs. The main task of transformer is
 * to transform inputs to outputs.
 * 
 * @author Petyr
 * @see Transform
 */
@Deprecated
public interface TransformContext
		extends ProcessingContext, ContextInputs, ContextOutputs {

}