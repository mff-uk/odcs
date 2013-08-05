package com.example;

import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;

public class DPUTemplate extends ConfigurableBase<DPUTemplateConfig>
		// TODO 1: Implements Extract,Transform or Load interface
		implements
		/* Extract|Transform|Load, */ConfigDialogProvider<DPUTemplateConfig> {

	public DPUTemplate() {
		super(new DPUTemplateConfig());
	}

	@Override
	public AbstractConfigDialog<DPUTemplateConfig> getConfigurationDialog() {
		return new DPUTemplateDialog();
	}

	// TODO 2: Provide implementation of unimplemented methods
}
