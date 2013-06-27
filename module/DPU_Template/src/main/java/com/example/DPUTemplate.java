package com.example;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;

public class DPUTemplate implements // TODO 1: Implements Extract,Transform or Load interface
	Configurable<DPUTemplateConfig>, ConfigDialogProvider<DPUTemplateConfig> {

	/**
	 * DPU's configuration.
	 */
	private DPUTemplateConfig config = new DPUTemplateConfig();
	
	@Override
	public AbstractConfigDialog<DPUTemplateConfig> getConfigurationDialog() {		
		return new DPUTemplateDialog();
	}

	@Override
	public void configure(DPUTemplateConfig c) throws ConfigException {
		config = c;		
	}

	@Override
	public DPUTemplateConfig getConfiguration() {
		return config;
	}
	
	// TODO 2: Provide implementation of unimplemented methods 
}
