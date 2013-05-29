package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.web.GraphicalExtractor;

public abstract class AbstractExtractor extends AbstractDPU implements GraphicalExtractor {

	@Override
	public DpuType getType() {
		return DpuType.EXTRACTOR;
	}	
	
}
