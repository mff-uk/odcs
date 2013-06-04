package module;

import gui.ConfigDialog;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.module.dpu.AbstractTransformer;
import cz.cuni.xrg.intlib.commons.module.gui.AbstractConfigDialog;

public class DPU_Template extends AbstractTransformer { // TODO 1: Select Abstract class to Exnted

	@Override
	public void saveConfigurationDefault(Config configuration) {
		// TODO 2: Store default configuration into configuration	
		configuration.setValue("myNumer", 3);
		configuration.setValue("myString", "Config ...");
	}

	@Override
	public AbstractConfigDialog createConfigurationDialog() {
		// TODO 3: Change to your configuration dialog. If you use gui.ConfigDialog this can be leave unchanged
		return new ConfigDialog();
	}

	// TODO 4: Implement extract/transform/load method based on super class selected in 1:
	
}
