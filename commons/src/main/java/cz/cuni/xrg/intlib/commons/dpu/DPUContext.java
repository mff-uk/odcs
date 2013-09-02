package cz.cuni.xrg.intlib.commons.dpu;

import cz.cuni.xrg.intlib.commons.context.ContextInputs;
import cz.cuni.xrg.intlib.commons.context.ContextOutputs;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;

/**
 * Context used by {@link DPU} during their execution process. The context
 * provide possibility to work with inputs as well as outputs.
 * 
 * @author Petyr
 * @see DPU
 *
 */
public interface DPUContext
	extends ProcessingContext, ContextOutputs, ContextInputs {

}
