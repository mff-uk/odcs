package cz.cuni.xrg.intlib.commons.module.dpu;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.web.GraphicalLoader;

public abstract class AbstractLoader extends AbstractDPU implements GraphicalLoader {

	@Override
	public DpuType getType() {
		return DpuType.LOADER;
	}

}
