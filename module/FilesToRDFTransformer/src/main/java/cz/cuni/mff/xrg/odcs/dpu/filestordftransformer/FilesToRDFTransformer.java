package cz.cuni.mff.xrg.odcs.dpu.filestordftransformer;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class FilesToRDFTransformer extends ConfigurableBase<FilesToRDFTransformerConfig> implements ConfigDialogProvider<FilesToRDFTransformerConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToRDFTransformer.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    public FilesToRDFTransformer() {
        super(FilesToRDFTransformerConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: commitSize: %d", config.getCommitSize());
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);


        RepositoryConnection connection = null;
        try {
            FilesDataUnit.Iteration filesIteration = filesInput.getIteration();

            if (!filesIteration.hasNext()) {
                return;
            }

            while (filesIteration.hasNext()) {
                connection = rdfOutput.getConnection();

                RDFInserter rdfInserter = new CancellableCommitSizeInserter(connection, config.getCommitSize(), dpuContext);
                rdfInserter.enforceContext(rdfOutput.getWriteDataGraph());

                ParseErrorListenerEnabledRDFLoader loader = new ParseErrorListenerEnabledRDFLoader(connection.getParserConfig(), connection.getValueFactory());

                FilesDataUnit.Entry entry = filesIteration.next();
                try {
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Starting extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFileURIString());
                    }
//                    ParseErrorCollector parseErrorCollector= new ParseErrorCollector();
                    
                    loader.load(new File(URI.create(entry.getFileURIString())), null, Rio.getParserFormatForFileName(entry.getSymbolicName()), rdfInserter, new ParseErrorLogger());

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Finished extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFileURIString());
                    }
                } catch (RDFHandlerException | RDFParseException | IOException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when extracting.",
                            "Symbolic name " + entry.getSymbolicName() + " path URI " + entry.getFileURIString(), ex);
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
        } catch (DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when extracting.", "", ex);
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
    public AbstractConfigDialog<FilesToRDFTransformerConfig> getConfigurationDialog() {
        return new FilesToRDFTransformerConfigDialog();
    }
}
