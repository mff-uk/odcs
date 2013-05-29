package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.web.GraphicalTransformer;

public abstract class AbstractTransformer extends AbstractDPU implements GraphicalTransformer {

	@Override
	public DpuType getType() {
		return DpuType.TRANSFORMER;
	}	
	
}
