package cz.cuni.mff.xrg.odcs.dpu.rdftordfmerger2transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import eu.unifiedviews.dataunit.DataUnitException;

@DPU.AsTransformer
public class RDFToRDFMerger2Transformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(RDFToRDFMerger2Transformer.class);

    @DataUnit.AsInput(name = "rdfInput")
    public RDFDataUnit rdfInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    public RDFToRDFMerger2Transformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage);
        LOG.info(shortMessage);
        
        try {
            ((ManagableDataUnit) rdfOutput).merge(rdfInput);
        } catch (DataUnitException ex) {
            throw new DPUException(ex);
        }
    }
}
