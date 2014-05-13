package cz.cuni.mff.xrg.odcs.extractor.rdf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;

/**
 * Test funcionality extaction for Virtuoso from SPARQL endpoint.
 * 
 * @author Jiri Tomes
 */
public class SPARQLExtractorVirtuosoSysTest {

    private final Logger logger = LoggerFactory.getLogger(
            SPARQLExtractorVirtuosoSysTest.class);

    private static TestEnvironment testEnvironment = new TestEnvironment();

    private static final String QUERY_ENDPOINT = "http://localhost:8890/sparql";

    @BeforeClass
    public static void setRDFDataUnit() throws RDFException {

    }

    private ExtractorEndpointParams getVirtuosoEndpoint() {
        return new ExtractorEndpointParams();
    }

    @AfterClass
    public static void deleteRDFDataUnit() {
        testEnvironment.release();
    }

    @Test
    public void extractNSoud() throws RepositoryException {
        try {
            RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
            RDFFormat usedRDFFormat = RDFFormat.NTRIPLES;

            URL endpointURL = new URL("http://internal.xrg.cz:8890/sparql");
            String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/legislation/nsoud.cz";
            String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o} limit 5000";

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getContexts());

            ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
            virtuoso.addDefaultGraph(defaultGraphUri);

            try {
                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);
                extractor.extractFromSPARQLEndpoint(endpointURL,
                        query, "", "", usedRDFFormat,
                        HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE,
                        true);

            } catch (RDFException e) {
                fail(e.getMessage());
            }

            long sizeAfter = connection.size(repository.getContexts());

            assertTrue(sizeBefore < sizeAfter);
            connection.close();
        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    //@Test
    public void extractBigDataFromEndpoint() throws RepositoryException {

        try {
            RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
            URL endpointURL = new URL("http://internal.opendata.cz:8890/sparql");
            String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/seznam.gov.cz/ovm/list/notransform";
            String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}";

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getContexts());

            ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
            virtuoso.addDefaultGraph(defaultGraphUri);

            try {
                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);
                extractor
                        .extractFromSPARQLEndpoint(endpointURL, defaultGraphUri,
                                query);

            } catch (RDFException e) {
                fail(e.getMessage());
            }

            long sizeAfter = connection.size(repository.getContexts());

            assertTrue(sizeBefore < sizeAfter);
            connection.close();
        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    @Test
    public void extractDataFromSPARQLEndpointTest() throws RepositoryException {

        try {
            RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

            URL endpointURL = new URL("http://dbpedia.org/sparql");
            String defaultGraphUri = "http://dbpedia.org";
            String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 50";

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getContexts());

            ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
            virtuoso.addDefaultGraph(defaultGraphUri);

            try {
                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);

                extractor
                        .extractFromSPARQLEndpoint(endpointURL, query);
            } catch (RDFException e) {
                fail(e.getMessage());
            }

            long sizeAfter = connection.size(repository.getContexts());

            assertTrue(sizeBefore < sizeAfter);
            connection.close();
        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    /**
     * This is not unit test, as it depends on remote server -> commented out
     * for build, use only when debugging
     */
    @Test
    public void extractDataFromSPARQLEndpointNamePasswordTest() throws RepositoryException {
        try {
            RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

            URL endpoint = new URL(QUERY_ENDPOINT.toString());
            String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 10";

            RDFFormat format = RDFFormat.N3;

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getContexts());

            ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();

            try {
                SPARQLExtractor extractor = new SPARQLExtractor(repository,
                        testEnvironment.getContext(), virtuoso);
                extractor.extractFromSPARQLEndpoint(
                        endpoint, query, "", "",
                        format);
            } catch (RDFException e) {
                fail(e.getMessage());
            }

            long sizeAfter = connection.size(repository.getContexts());

            assertTrue(sizeBefore < sizeAfter);
            connection.close();
        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

}
