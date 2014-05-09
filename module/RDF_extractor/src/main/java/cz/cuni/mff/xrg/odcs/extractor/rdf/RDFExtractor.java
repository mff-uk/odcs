package cz.cuni.mff.xrg.odcs.extractor.rdf;

import java.net.MalformedURLException;
import java.net.URL;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;

/**
 * Extracts RDF data from SPARQL endpoint.
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
@AsExtractor
public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig>
        implements ConfigDialogProvider<RDFExtractorConfig> {

    private final Logger LOG = LoggerFactory.getLogger(RDFExtractor.class);

    /**
     * The repository for SPARQL extractor.
     */
    @OutputDataUnit
    public RDFDataUnit rdfDataUnit;

    public RDFExtractor() {
        super(RDFExtractorConfig.class);
    }

    /**
     * Execute the SPARQL extractor.
     * 
     * @param context
     *            SPARQL extractor context.
     * @throws DataUnitException
     *             if this DPU fails.
     * @throws DPUException
     *             if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException,
            DataUnitCreateException {
        RepositoryConnection connection = null;

        try {
            final URL endpointURL = new URL(config.getSPARQLEndpoint());
            final String hostName = config.getHostName();
            final String password = config.getPassword();
            String constructQuery = config.getSPARQLQuery();

            final boolean usedSplitConstruct = config.isUsedSplitConstruct();

            if (constructQuery.isEmpty()) {
                constructQuery = "construct {?x ?y ?z} where {?x ?y ?z}";
            }

            boolean useStatisticHandler = config.isUsedStatisticalHandler();
            boolean failWhenErrors = config.isFailWhenErrors();

            HandlerExtractType handlerExtractType = HandlerExtractType
                    .getHandlerType(useStatisticHandler, failWhenErrors);

            final boolean extractFail = config.isExtractFail();

            Integer retrySize = config.getRetrySize();
            if (retrySize == null) {
                retrySize = -1;
                LOG.info("retrySize is null, using -1 instead");
            }
            Long retryTime = config.getRetryTime();
            if (retryTime == null) {
                retryTime = 1000L;
                LOG.info("retryTime is null, using 1000 instead");
            }

            ExtractorEndpointParams endpointParams = config.getEndpointParams();

            if (endpointParams == null) {
                endpointParams = new ExtractorEndpointParams();
                LOG.info(
                        "Extractor endpoint params is null, used default values instead without setting ");
            }

            Integer splitConstructSize = config.getSplitConstructSize();
            if (splitConstructSize == null) {
                splitConstructSize = 50000;
                LOG.info("Split construct size is null, using 50000");
            }

            SPARQLExtractor extractor = new SPARQLExtractor(rdfDataUnit, context,
                    retrySize, retryTime, endpointParams);

            long lastrepoSize = 0;
            connection = rdfDataUnit.getConnection();
            lastrepoSize = connection.size(rdfDataUnit.getDataGraph());

            if (usedSplitConstruct) {
                if (splitConstructSize <= 0) {
                    context.sendMessage(MessageType.ERROR,
                            "Split construct size must be positive number");
                }

                SplitConstructQueryHelper helper = new SplitConstructQueryHelper(
                        constructQuery, splitConstructSize);

                LOG.debug(
                        "The max size of one data part extracted from SPARQL extractor is set to {} TRIPLES",
                        splitConstructSize);
                while (true) {
                    String splitConstructQuery = helper.getSplitConstructQuery();

                    extractor.extractFromSPARQLEndpoint(endpointURL,
                            splitConstructQuery,
                            hostName, password, RDFFormat.NTRIPLES,
                            handlerExtractType, false);

                    long newrepoSize = connection.size(rdfDataUnit.getDataGraph());

                    checkParsingProblems(useStatisticHandler, context);
                    if (lastrepoSize < newrepoSize) {
                        lastrepoSize = newrepoSize;
                        helper.goToNextQuery();
                    } else {
                        break;
                    }
                }

                if (extractFail && lastrepoSize == 0) {
                    throw new RDFException(
                            "No extracted triples from SPARQL endpoint");
                }

            } else {

                extractor.extractFromSPARQLEndpoint(endpointURL, constructQuery,
                        hostName, password, RDFFormat.NTRIPLES,
                        handlerExtractType, extractFail);

                checkParsingProblems(useStatisticHandler, context);
            }
            final long triplesCount = connection.size(rdfDataUnit.getDataGraph());

            String tripleInfoMessage = String.format(
                    "Extracted %s triples from SPARQL endpoint %s",
                    triplesCount, endpointURL.toString());

            context.sendMessage(MessageType.INFO, tripleInfoMessage);

        } catch (InvalidQueryException ex) {
            LOG.debug("InvalidQueryException", ex);
            context.sendMessage(MessageType.ERROR,
                    "InvalidQueryException: " + ex.getMessage());
        } catch (MalformedURLException ex) {
            LOG.debug("RDFDataUnitException", ex);
            context.sendMessage(MessageType.ERROR, "MalformedURLException: "
                    + ex.getMessage());
            throw new DPUException(ex);
        } catch (RDFDataUnitException ex) {
            context.sendMessage(MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } catch (RepositoryException e) {
            context.sendMessage(MessageType.ERROR,
                    "connection to repository broke down");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(MessageType.ERROR, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }
    }

    private void checkParsingProblems(boolean useStatisticHandler,
            DPUContext context) {

        if (useStatisticHandler && StatisticalHandler.hasParsingProblems()) {

            String problems = StatisticalHandler
                    .getFoundGlobalProblemsAsString();
            StatisticalHandler.clearParsingProblems();

            context.sendMessage(MessageType.WARNING,
                    "Statistical and error handler has found during parsing problems triples (these triples were not added)",
                    problems);
        }
    }

    /**
     * Returns the configuration dialogue for SPARQL extractor.
     * 
     * @return the configuration dialogue for SPARQL extractor.
     */
    @Override
    public AbstractConfigDialog<RDFExtractorConfig> getConfigurationDialog() {
        return new RDFExtractorDialog();
    }
}
