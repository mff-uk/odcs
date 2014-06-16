package cz.cuni.mff.xrg.odcs.dpu.triplegeneratortordfextractor;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;

@AsExtractor
public class TripleGeneratorToRDFExtractor extends ConfigurableBase<TripleGeneratorToRDFExtractorConfig> implements ConfigDialogProvider<TripleGeneratorToRDFExtractorConfig> {
	private static final Logger LOG = LoggerFactory.getLogger(TripleGeneratorToRDFExtractor.class);
	
    public TripleGeneratorToRDFExtractor() {
        super(TripleGeneratorToRDFExtractorConfig.class);
    }

    @OutputDataUnit(name = "output")
    public WritableRDFDataUnit rdfOutput;

    @Override
    public void execute(DPUContext dpuContext)
            throws DPUException,
            DataUnitException {
    	String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(MessageType.INFO, shortMessage);
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
                        ), rdfOutput.getWriteContext());
                if ((i % 25000) == 0) {
                    connection.commit();
                    dpuContext.sendMessage(MessageType.DEBUG, "Number of triples " + String.valueOf(i));
                    if (dpuContext.canceled()) {
                        break;
                    }
                    connection.begin();
                }
            }
            connection.commit();
            dpuContext.sendMessage(MessageType.DEBUG, "Number of triples " + String.valueOf(connection.size(rdfOutput.getWriteContext())));
        } catch (RepositoryException ex) {
            dpuContext.sendMessage(MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    dpuContext.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }
    }

    @Override
    public AbstractConfigDialog<TripleGeneratorToRDFExtractorConfig> getConfigurationDialog() {
        return new TripleGeneratorToRDFExtractorConfigDialog();
    }
}
