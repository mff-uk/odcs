package com.example;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;
import cz.cuni.xrg.intlib.commons.dpu.annotation.*;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

// TODO You can choose AsLoader or AsExtractor instead of AsTransformer
@AsTransformer
public class DPUTemplate extends ConfigurableBase<DPUTemplateConfig>
		implements DPU, 
		// TODO If you do not want the dialog, delete the following line
		// 	and getConfigurationDialog function
		ConfigDialogProvider<DPUTemplateConfig> 
	{
	
	@InputDataUnit
	public RDFDataUnit rdfInput;
	
	@OutputDataUnit
	public RDFDataUnit rdfOutput;
	
	public DPUTemplate() {
		super(DPUTemplateConfig.class);
	}

	@Override
	public AbstractConfigDialog<DPUTemplateConfig> getConfigurationDialog() {
		return new DPUTemplateDialog();
	}

	@Override
	public void execute(DPUContext context)
			throws DPUException,
				DataUnitException {
		// TODO Execute your DPU here
		
		// DPU's configuration is accessible under 'this.config' 
	}
	
}
