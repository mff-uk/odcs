package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.validators.RepositoryDataValidator;

/**
 * Loads RDF data to SPARQL endpoint.
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
@AsLoader
public class RDFLoader extends ConfigurableBase<RDFLoaderConfig>
        implements ConfigDialogProvider<RDFLoaderConfig> {

    private final Logger LOG = LoggerFactory.getLogger(RDFLoader.class);

    /**
     * The repository for SPARQL loader.
     */
    @InputDataUnit(name = "input")
    public RDFDataUnit rdfDataUnit;

    @OutputDataUnit(name = "input_redirection", optional = true)
    public WritableRDFDataUnit inputShadow;

    @OutputDataUnit(name = "validationDataUnit", description = "Never connect any data to this unit please!")
    public WritableRDFDataUnit validationDataUnit;

    public RDFLoader() {
        super(RDFLoaderConfig.class);
    }

    /**
     * Execute the SPARQL loader.
     * 
     * @param context
     *            SPARQL loader context.
     * @throws DataUnitException
     *             if this DPU fails.
     * @throws DPUException
     *             if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException,
            DataUnitException {

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

            context.sendMessage(MessageType.INFO,
                    "Starting RDF data VALIDATION");

            DataValidator dataValidator = new RepositoryDataValidator(
                    rdfDataUnit, validationDataUnit);

            if (!dataValidator.areDataValid()) {
                final String message = "RDF Data are NOT VALID - LOADING to SPARQL FAIL";
                LOG.error(dataValidator.getErrorMessage());

                context.sendMessage(MessageType.INFO, message, dataValidator
                        .getErrorMessage());

                throw new DPUException(message);
            } else {
                context.sendMessage(MessageType.INFO,
                        "RDF Data VALIDATION SUCCESFULL");
                context.sendMessage(MessageType.INFO,
                        "Loading data to SPARQL endpoint STARTS JUST NOW");
            }
        }

        RepositoryConnection connection = null;
        long triplesCount = 0;
        try {
            connection = rdfDataUnit.getConnection();
            triplesCount = connection.size(rdfDataUnit.getContexts().toArray(new URI[0]));
        } catch (RepositoryException e) {
            context.sendMessage(MessageType.ERROR,
                    "connection to repository broke down");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }

        String tripleInfoMessage = String.format(
                "Prepare for loading %s triples to SPARQL endpoint %s",
                triplesCount,
                endpointURL.toString());

        context.sendMessage(MessageType.INFO, tripleInfoMessage);

        try {
            SPARQLoader loader = new SPARQLoader(rdfDataUnit, context, config);

            loader.loadToSPARQLEndpoint();

        } catch (DPUException ex) {
            context.sendMessage(MessageType.ERROR, ex.getMessage(), "", ex);
        }

        if (config.isPenetrable()) {
            inputShadow.addAll(rdfDataUnit);
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
