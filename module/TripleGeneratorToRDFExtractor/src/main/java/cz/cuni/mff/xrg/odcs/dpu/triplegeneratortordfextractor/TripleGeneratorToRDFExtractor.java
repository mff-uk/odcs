package cz.cuni.mff.xrg.odcs.dpu.triplegeneratortordfextractor;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsExtractor
public class TripleGeneratorToRDFExtractor extends ConfigurableBase<TripleGeneratorToRDFExtractorConfig> implements ConfigDialogProvider<TripleGeneratorToRDFExtractorConfig> {
	private static final Logger LOG = LoggerFactory.getLogger(TripleGeneratorToRDFExtractor.class);
	
    public TripleGeneratorToRDFExtractor() {
        super(TripleGeneratorToRDFExtractorConfig.class);
    }

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit rdfOutput;

    @Override
    public void execute(DPUContext dpuContext)
            throws DPUException {
    	String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage);
        LOG.info(shortMessage);
        
        RepositoryConnection connection = null;
        try {
            connection = rdfOutput.getConnection();
            ValueFactory f = new MemValueFactory();
            connection.begin();
            int j = 1;
            for (int i = 0; i < config.getTripleCount(); i++) {
                connection.add(f.createStatement(
                        f.createURI("http://example.org/people/d" + String.valueOf(j++)),
                        f.createURI("http://example.org/ontology/e" + String.valueOf(j++)),
                        f.createLiteral("Alice" + String.valueOf(j++))
                        ), rdfOutput.getBaseDataGraphURI());
                if ((i % 25000) == 0) {
                    connection.commit();
                    dpuContext.sendMessage(DPUContext.MessageType.DEBUG, "Number of triples " + String.valueOf(i));
                    if (dpuContext.canceled()) {
                        break;
                    }
                    connection.begin();
                }
            }
            connection.commit();
            dpuContext.sendMessage(DPUContext.MessageType.DEBUG,
                    "Number of triples " + String.valueOf(connection.size(rdfOutput.getBaseDataGraphURI())));
        } catch (RepositoryException | DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }
    }

    @Override
    public AbstractConfigDialog<TripleGeneratorToRDFExtractorConfig> getConfigurationDialog() {
        return new TripleGeneratorToRDFExtractorConfigDialog();
    }
}
