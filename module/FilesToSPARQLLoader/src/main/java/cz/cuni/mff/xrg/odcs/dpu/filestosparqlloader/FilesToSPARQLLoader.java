package cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsLoader
public class FilesToSPARQLLoader extends ConfigurableBase<FilesToSPARQLLoaderConfig> implements ConfigDialogProvider<FilesToSPARQLLoaderConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToSPARQLLoader.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    public FilesToSPARQLLoader() {
        super(FilesToSPARQLLoaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: CommitSize: %d, QueryEndpointUrl: %s, UpdateEndpointUrl: %s", config.getCommitSize(), config.getQueryEndpointUrl(), config.getUpdateEndpointUrl());
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        Set<String> targetContexts = config.getTargetContexts();
        URI[] targetContextsURIs = new URI[targetContexts.size()];
        int i = 0;
        for (String contextString : targetContexts) {
            targetContextsURIs[i] = new URIImpl(contextString);
            i++;
        }

        FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = filesInput.getIteration();
        } catch (DataUnitException ex) {
            throw new DPUException("Could not obtain filesInput", ex);
        }

        SPARQLRepository sparqlRepository = new SPARQLRepository(config.getQueryEndpointUrl(), config.getUpdateEndpointUrl());
        try {
            sparqlRepository.initialize();
        } catch (RepositoryException ex) {
            throw new DPUException("Could not initialize remote repository", ex);
        }

        long filesSuccessfulCount = 0L;
        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();
        try {
            while ((shouldContinue) && (filesIteration.hasNext())) {
                FilesDataUnit.Entry entry;
                try {
                    entry = filesIteration.next();
                    RepositoryConnection connection = null;
                    try {
                        Date start = new Date();
                        connection = sparqlRepository.getConnection();

                        RDFInserter rdfInserter = new CancellableCommitSizeInserter(connection, config.getCommitSize(), dpuContext);
                        if (targetContextsURIs.length > 0) {
                            rdfInserter.enforceContext(targetContextsURIs);
                        }

                        RDFLoader loader = new RDFLoader(connection.getParserConfig(), connection.getValueFactory());
                        if (dpuContext.isDebugging()) {
                            LOG.debug("Processing {} file {}", appendNumber(index), entry);
                        }

                        loader.load(new File(java.net.URI.create(entry.getFileURIString())), null, null, rdfInserter);

                        if (dpuContext.isDebugging()) {
                            LOG.debug("Processed {} file in {}s", appendNumber(index), (System.currentTimeMillis() - start.getTime()) / 1000);
                        }
                        filesSuccessfulCount++;
                    } catch (RepositoryException | RDFHandlerException | RDFParseException | IOException ex) {
                        dpuContext.sendMessage(
                                config.isSkipOnError() ? DPUContext.MessageType.WARNING : DPUContext.MessageType.ERROR,
                                "Error processing " + appendNumber(index) + " file",
                                String.valueOf(entry),
                                ex);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (RepositoryException ex) {
                                LOG.warn("Error closing connection", ex);
                            }
                        }
                    }
                } catch (DataUnitException ex) {
                    dpuContext.sendMessage(
                            config.isSkipOnError() ? DPUContext.MessageType.WARNING : DPUContext.MessageType.ERROR,
                            "DataUnit exception.",
                            "",
                            ex);
                }

                shouldContinue = !dpuContext.canceled();
            }
        } catch (DataUnitException ex) {
            throw new DPUException("Error iterating filesInput.", ex);
        } finally {
            try {
                filesIteration.close();
            } catch (DataUnitException ex) {
                dpuContext.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
            }
            try {
                sparqlRepository.shutDown();
            } catch (RepositoryException ex) {
                dpuContext.sendMessage(DPUContext.MessageType.WARNING, "Error shutting down the remote SPARQL repository", ex.getMessage(), ex);
            }
        }
        String message = String.format("Processed %d/%d", filesSuccessfulCount, index);
        dpuContext.sendMessage(filesSuccessfulCount < index ? DPUContext.MessageType.WARNING : DPUContext.MessageType.INFO, message);
    }

    @Override
    public AbstractConfigDialog<FilesToSPARQLLoaderConfig> getConfigurationDialog() {
        return new FilesToSPARQLLoaderConfigDialog();
    }

    public static String appendNumber(long number) {
        String value = String.valueOf(number);
        if (value.length() > 1) {
            // Check for special case: 11 - 13 are all "th".
            // So if the second to last digit is 1, it is "th".
            char secondToLastDigit = value.charAt(value.length() - 2);
            if (secondToLastDigit == '1')
                return value + "th";
        }
        char lastDigit = value.charAt(value.length() - 1);
        switch (lastDigit) {
            case '1':
                return value + "st";
            case '2':
                return value + "nd";
            case '3':
                return value + "rd";
            default:
                return value + "th";
        }
    }
}
