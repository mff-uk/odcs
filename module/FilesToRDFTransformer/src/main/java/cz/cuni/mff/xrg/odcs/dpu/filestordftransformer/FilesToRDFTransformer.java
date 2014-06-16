package cz.cuni.mff.xrg.odcs.dpu.filestordftransformer;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesIteration;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;

@AsTransformer
public class FilesToRDFTransformer extends ConfigurableBase<FilesToRDFTransformerConfig> implements ConfigDialogProvider<FilesToRDFTransformerConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToRDFTransformer.class);

    @InputDataUnit(name = "filesInput")
    public FilesDataUnit filesInput;

    @OutputDataUnit(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    public FilesToRDFTransformer() {
        super(FilesToRDFTransformerConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: commitSize: %d", config.getCommitSize());
        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);
        FilesIteration filesIteration = filesInput.getFiles();

        if (!filesIteration.hasNext()) {
            return;
        }

        RepositoryConnection connection = null;
        try {
            while (filesIteration.hasNext()) {
                connection = rdfOutput.getConnection();

                RDFInserter rdfInserter = new CommitSizeInserter(connection, config.getCommitSize());
                rdfInserter.enforceContext(rdfOutput.getWriteContext());

                ParseErrorListenerEnabledRDFLoader loader = new ParseErrorListenerEnabledRDFLoader(connection.getParserConfig(), connection.getValueFactory());

                FilesDataUnitEntry entry = filesIteration.next();
                try {
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Starting extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI());
                    }
//                    ParseErrorCollector parseErrorCollector= new ParseErrorCollector();
                    loader.load(new File(entry.getFilesystemURI()), null, null, rdfInserter, new ParseErrorLogger());

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Finished extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI());
                    }
                } catch (RDFHandlerException | RDFParseException | IOException ex) {
                    dpuContext.sendMessage(MessageType.ERROR, "Error when extracting.", "Symbolic name " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI(), ex);
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
        } catch (RepositoryException ex) {
            dpuContext.sendMessage(MessageType.ERROR, "Error when extracting.", "", ex);
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
    public AbstractConfigDialog<FilesToRDFTransformerConfig> getConfigurationDialog() {
        return new FilesToRDFTransformerConfigDialog();
    }
}
