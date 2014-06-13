package cz.cuni.mff.xrg.odcs.dpu.rdftordfmerger2transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;

@AsTransformer
public class RDFToRDFMerger2Transformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(RDFToRDFMerger2Transformer.class);

    @InputDataUnit(name = "rdfInput")
    public RDFDataUnit rdfInput;

    @OutputDataUnit(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    public RDFToRDFMerger2Transformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getName() + " starting.";
        dpuContext.sendMessage(MessageType.INFO, shortMessage);
        LOG.info(shortMessage);
        
        ((ManagableDataUnit) rdfOutput).merge(rdfInput);
    }
}
