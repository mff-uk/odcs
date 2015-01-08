package eu.unifiedviews.plugins.transformer.rdfmerger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dpu.NonConfigurableBase;

@DPU.AsTransformer
public class RdfMerger extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(RdfMerger.class);

    @DataUnit.AsInput(name = "rdfInput")
    public RDFDataUnit rdfInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    public RdfMerger() {
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
