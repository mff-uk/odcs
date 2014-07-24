package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * Loads RDF data to SPARQL endpoint.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@DPU.AsLoader
public class RDFLoader extends ConfigurableBase<RDFLoaderConfig>
        implements ConfigDialogProvider<RDFLoaderConfig> {

    private final Logger LOG = LoggerFactory.getLogger(RDFLoader.class);

    /**
     * The repository for SPARQL loader.
     */
    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inputDataUnit;

    @DataUnit.AsOutput(name = "input_redirection", optional = true)
    public WritableRDFDataUnit inputShadow;

    @DataUnit.AsOutput(name = "validationDataUnit", description = "Never connect any data to this unit please!")
    public WritableRDFDataUnit validationDataUnit;

    public RDFLoader() {
        super(RDFLoaderConfig.class);
    }

    /**
     * Execute the SPARQL loader.
     *
     * @param context SPARQL loader context.
     * @throws DPUException if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException {

        final String endpointURL = config.getSPARQLEndpoint();
        final List<String> defaultGraphsURI = config.getGraphsUri();
        final String hostName = config.getHostName();
        final String password = config.getPassword();
        final WriteGraphType graphType = config.getGraphOption();
        final InsertType insertType = config.getInsertOption();
        final long chunkSize = config.getChunkSize();
        final boolean validateDataBefore = config.isValidDataBefore();

        //check that SPARQL endpoint URL is correct
        ParamController.testEndpointSyntax(endpointURL);

        ParamController.testNullParameter(defaultGraphsURI,
                "Default graph must be specifed");
        ParamController.testEmptyParameter(defaultGraphsURI,
                "Default graph must be specifed");

        ParamController.testPositiveParameter(chunkSize,
                "Chunk size must be number greater than 0");

        LoaderEndpointParams endpointParams = config.getEndpointParams();

        if (endpointParams == null) {
            endpointParams = new LoaderEndpointParams();
            LOG.info(
                    "Loader endpoint params is null, used default values instead");
        }

        Integer retrySize = config.getRetrySize();
        if (retrySize == null) {
            retrySize = -1;
            LOG.info("retrySize is null, using -1 instead");
        }
        Long retryTime = config.getRetryTime();
        if (retryTime == null) {
            retryTime = (long) 1000;
            LOG.info("retryTime is null, using 1000 instead");
        }

        if (validateDataBefore) {
            context.sendMessage(MessageType.ERROR, "Data validation is unsupported, use data validation DPU");
        }

        RepositoryConnection connection = null;
        long triplesCount = 0;
        try {
            connection = inputDataUnit.getConnection();
            triplesCount = connection.size(inputDataUnit.getDataGraphnames().toArray(new URI[0]));
        } catch (RepositoryException e) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "connection to repository broke down");
        } catch (DataUnitException e) {
            throw new DPUException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }

        String tripleInfoMessage = String.format(
                "Prepare for loading %s triples to SPARQL endpoint %s",
                triplesCount,
                endpointURL);

        context.sendMessage(DPUContext.MessageType.INFO, tripleInfoMessage);

        try {
            SPARQLoader loader = new SPARQLoader(inputDataUnit, context, config);

            loader.loadToSPARQLEndpoint();

        } catch (DPUException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, ex.getMessage(), "", ex);
        }

        if (config.isPenetrable()) {
            try {
                inputShadow.addAll(inputDataUnit);
            } catch (DataUnitException e) {
                throw new DPUException(e);
            }
        }
    }

    /**
     * Returns the configuration dialogue for SPARQL loader.
     *
     * @return the configuration dialogue for SPARQL loader.
     */
    @Override
    public AbstractConfigDialog<RDFLoaderConfig> getConfigurationDialog() {
        return new RDFLoaderDialog();
    }
}
