package cz.cuni.mff.xrg.odcs.extractor.rdf;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPUException;

/**
 * Test extraction functionality for Local RDF storage from SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public class SPARQLExtractorLocalSysTest {

    private static final Logger LOG = LoggerFactory.getLogger(
            SPARQLExtractorLocalSysTest.class);

    private static TestEnvironment testEnvironment = new TestEnvironment();

    private static final String QUERY_ENDPOINT = "http://localhost:8890/sparql";

    private ExtractorEndpointParams getVirtuosoEndpoint() {
        return new ExtractorEndpointParams();
    }

    @AfterClass
    public static void deleteRDFDataUnit() {
        testEnvironment.release();
    }

    //@Test
    public void extractBigDataFromEndpoint() throws RepositoryException, DataUnitException {

        try {
            WritableRDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
            URL endpointURL = new URL("http://internal.opendata.cz:8890/sparql");
            String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/seznam.gov.cz/ovm/list/notransform";
            String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}";

            RepositoryConnection connection = null;
            try {
                connection = repository.getConnection();
                long sizeBefore = connection.size(repository.getBaseDataGraphURI());
                ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
                virtuoso.addDefaultGraph(defaultGraphUri);

                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);

                extractor.extractFromSPARQLEndpoint(endpointURL, query);

                long sizeAfter = connection.size(repository.getBaseDataGraphURI());

                assertTrue(sizeBefore < sizeAfter);
            } catch (DPUException ex) {
                LOG.error("RDFException: " + ex.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable ex) {
                        LOG.warn("Error closing connection", ex);
                    }
                }
            }

        } catch (MalformedURLException ex) {
            LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    //@Test
    public void extractDataFromSPARQLEndpointTest() throws RepositoryException, DataUnitException {

        try {
            WritableRDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
            URL endpointURL = new URL("http://dbpedia.org/sparql");
            String defaultGraphUri = "http://dbpedia.org";
            String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 50";

            RepositoryConnection connection = null;
            try {
                connection = repository.getConnection();
                long sizeBefore = connection.size(repository.getBaseDataGraphURI());

                ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
                virtuoso.addDefaultGraph(defaultGraphUri);

                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);

                extractor.extractFromSPARQLEndpoint(endpointURL, query);

                long sizeAfter = connection.size(repository.getBaseDataGraphURI());

                assertTrue(sizeBefore < sizeAfter);
            } catch (DPUException ex) {
                LOG.error("RDFException: " + ex.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable ex) {
                        LOG.warn("Error closing connection", ex);
                    }
                }
            }

        } catch (MalformedURLException ex) {
            LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    /**
     * This is not unit test, as it depends on remote server -> commented out
     * for build, use only when debugging
     *
     * @throws org.openrdf.repository.RepositoryException
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    //@Test
    public void extractDataFromSPARQLEndpointNamePasswordTest() throws RepositoryException, DataUnitException {
        try {
            WritableRDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
            URL endpoint = new URL(QUERY_ENDPOINT.toString());
            String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 10";

            RDFFormat format = RDFFormat.N3;

            RepositoryConnection connection = null;
            try {
                connection = repository.getConnection();
                long sizeBefore = connection.size(repository.getBaseDataGraphURI());

                ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
                virtuoso.addDefaultGraph("http://BigGraph");

                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);
                extractor.extractFromSPARQLEndpoint(endpoint, query, "", "", format);

                long sizeAfter = connection.size(repository.getBaseDataGraphURI());

                assertTrue(sizeBefore < sizeAfter);

            } catch (DPUException ex) {
                LOG.error("RDFException: " + ex.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable ex) {
                        LOG.warn("Error closing connection", ex);
                    }
                }
            }
        } catch (MalformedURLException ex) {
            LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }
}
