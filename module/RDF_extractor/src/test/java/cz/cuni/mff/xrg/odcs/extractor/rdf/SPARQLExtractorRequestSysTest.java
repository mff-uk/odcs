package cz.cuni.mff.xrg.odcs.extractor.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;

/**
 * @author Jiri Tomes
 */
public class SPARQLExtractorRequestSysTest {

    private static final Logger LOG = LoggerFactory.getLogger(
            SPARQLExtractorRequestSysTest.class);

    private static final TestEnvironment testEnvironment = new TestEnvironment();

    private static final String ENDPOINT = "http://dbpedia.org/sparql";

    private static final String DEFAULT_GRAPH = "http://dbpedia.org";

    private static final String NAMED_GRAPH = "http://dbpedia.org/void";

    private int EXTRACTED_TRIPLES = 15;

    private URL getEndpoint() {
        URL endpoint = null;
        try {
            endpoint = new URL(ENDPOINT);
        } catch (MalformedURLException ex) {
            LOG.debug(ex.getMessage());
        }

        return endpoint;
    }

    private DPUContext getTestContext() {
        TestEnvironment environment = new TestEnvironment();
        return environment.getContext();
    }

    private void extractFromEndpoint(ExtractorEndpointParams params) throws RepositoryException, DataUnitException {
        WritableRDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
        URL endpoint = getEndpoint();
        String query = String.format(
                "CONSTRUCT {?x ?y ?z} WHERE {?x ?y ?z} LIMIT %s",
                EXTRACTED_TRIPLES);

        RepositoryConnection connection = null;
        try {
            SPARQLExtractor extractor = new SPARQLExtractor(repository,
                    getTestContext(), params);
            extractor.extractFromSPARQLEndpoint(endpoint, query);
            connection = repository.getConnection();
            assertEquals(connection.size(repository.getWriteDataGraph()), EXTRACTED_TRIPLES);
        } catch (RDFException ex) {
            fail(ex.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
        }
    }

    @AfterClass
    public static void deleteRDFDataUnit() {
        testEnvironment.release();
    }

    @Test
    public void GetSimpleTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "";
        String namedGraphParam = "";
        ExtractorRequestType requestType = ExtractorRequestType.GET_URL_ENCODER;

        ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
                defaultGraphParam, namedGraphParam, requestType);

        extractFromEndpoint(params);
    }

    @Test
    public void GetDefaultGraphParamTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "default-graph-uri";
        String namedGraphParam = "";
        ExtractorRequestType requestType = ExtractorRequestType.GET_URL_ENCODER;

        ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
                defaultGraphParam, namedGraphParam, requestType);

        params.addDefaultGraph(DEFAULT_GRAPH);

        extractFromEndpoint(params);
    }

    @Test
    public void GetAllGraphParamTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "default-graph-uri";
        String namedGraphParam = "named-graph-uri";
        ExtractorRequestType requestType = ExtractorRequestType.GET_URL_ENCODER;

        ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
                defaultGraphParam, namedGraphParam, requestType);

        params.addDefaultGraph(DEFAULT_GRAPH);
        params.addNamedGraph(NAMED_GRAPH);

        extractFromEndpoint(params);
    }

    @Test
    public void POSTEncodeSimpleTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "";
        String namedGraphParam = "";
        ExtractorRequestType requestType = ExtractorRequestType.POST_URL_ENCODER;

        ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
                defaultGraphParam, namedGraphParam, requestType);

        extractFromEndpoint(params);
    }

    @Test
    public void POSTEncodeDefaultGraphParamTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "default-graph-uri";
        String namedGraphParam = "";
        ExtractorRequestType requestType = ExtractorRequestType.POST_URL_ENCODER;

        ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
                defaultGraphParam, namedGraphParam, requestType);

        params.addDefaultGraph(DEFAULT_GRAPH);

        extractFromEndpoint(params);
    }

    @Test
    public void POSTEncodeAllGraphParamTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "default-graph-uri";
        String namedGraphParam = "named-graph-uri";
        ExtractorRequestType requestType = ExtractorRequestType.POST_URL_ENCODER;

        ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
                defaultGraphParam, namedGraphParam, requestType);

        params.addDefaultGraph(DEFAULT_GRAPH);
        params.addNamedGraph(NAMED_GRAPH);

        extractFromEndpoint(params);
    }

}
