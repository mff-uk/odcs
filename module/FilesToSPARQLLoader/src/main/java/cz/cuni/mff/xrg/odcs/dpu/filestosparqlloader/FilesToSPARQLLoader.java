package cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.repository.util.RDFLoader;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesIteration;

@AsLoader
public class FilesToSPARQLLoader extends ConfigurableBase<FilesToSPARQLLoaderConfig> implements ConfigDialogProvider<FilesToSPARQLLoaderConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToSPARQLLoader.class);

    @InputDataUnit(name = "fileInput")
    public FilesDataUnit fileInput;

    public FilesToSPARQLLoader() {
        super(FilesToSPARQLLoaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getName() + " starting.";
        String longMessage = String.format("Configuration: CommitSize: %d, QueryEndpointUrl: %s, UpdateEndpointUrl: %s", config.getCommitSize(), config.getQueryEndpointUrl(), config.getUpdateEndpointUrl());
        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        SPARQLRepository sparqlRepository = new SPARQLRepository(config.getQueryEndpointUrl(), config.getUpdateEndpointUrl());
        try {
            sparqlRepository.initialize();
        } catch (RepositoryException ex) {
            dpuContext.sendMessage(MessageType.ERROR, "Could not initialize remote SPARQL repository", ex.getMessage(), ex);
        } finally {
            try {
                sparqlRepository.shutDown();
            } catch (RepositoryException ex) {
                dpuContext.sendMessage(MessageType.WARNING, "Error shutting down the remote SPARQL repository", ex.getMessage(), ex);
            }
        }

        if (!sparqlRepository.isInitialized()) {
            return;
        }

        FilesIteration filesIteration = fileInput.getFiles();

        if (!filesIteration.hasNext()) {
            return;
        }

        RepositoryConnection connection = null;
        Set<String> targetContexts = config.getTargetContexts();
        URI[] targetContextsURIs = new URI[targetContexts.size()];
        int i = 0;
        for (String contextString : targetContexts) {
            targetContextsURIs[i] = new URIImpl(contextString);
            i++;
        }
        try {
            while (filesIteration.hasNext()) {
                checkCancelled(dpuContext);
                
                FilesDataUnitEntry entry = filesIteration.next();
                try {
                    connection = sparqlRepository.getConnection();

                    RDFInserter rdfInserter = new CancellableCommitSizeInserter(connection, config.getCommitSize(), dpuContext);
                    if (targetContextsURIs.length > 0) {
                        rdfInserter.enforceContext(targetContextsURIs);
                    }

                    RDFLoader loader = new RDFLoader(connection.getParserConfig(), connection.getValueFactory());
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Starting loading of file " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI());
                    }
                    loader.load(new File(entry.getFilesystemURI()), null, null, rdfInserter);

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Finished loading of file " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI());
                    }
                } catch (RDFHandlerException | RDFParseException | IOException ex) {
                    dpuContext.sendMessage(MessageType.ERROR, "Error when loading.", "Symbolic name " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI(), ex);
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
            dpuContext.sendMessage(MessageType.ERROR, "Error when loading.", ex.getMessage(), ex);
        } finally {
            filesIteration.close();
        }
    }

    @Override
    public AbstractConfigDialog<FilesToSPARQLLoaderConfig> getConfigurationDialog() {
        return new FilesToSPARQLLoaderConfigDialog();
    }
    
    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
        }
    }
}
