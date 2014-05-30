package com.example;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;

// TODO 1: You can choose AsLoader or AsExtractor instead of AsTransformer
@AsTransformer
public class DPUTemplate extends ConfigurableBase<DPUTemplateConfig>
        implements
        // If you do not want the dialog, delete the following line
        // 	and getConfigurationDialog function
        ConfigDialogProvider<DPUTemplateConfig>
{

    @InputDataUnit(name = "input")
    public RDFDataUnit rdfInput;

    @OutputDataUnit(name = "output")
    public WritableRDFDataUnit rdfOutput;

    public DPUTemplate() {
        super(DPUTemplateConfig.class);
    }

    @Override
    public AbstractConfigDialog<DPUTemplateConfig> getConfigurationDialog() {
        return new DPUTemplateDialog();
    }

    // TODO 2: Implement the method execute being called when the DPU is launched
    @Override
    public void execute(DPUContext context)
            throws DPUException,
            DataUnitException {

        // DPU's configuration is accessible under 'this.config' 
        // DPU's context is accessible under 'context'
        // DPU's data units are accessible under 'rdfInput' and 'rdfOutput'
    }

}
