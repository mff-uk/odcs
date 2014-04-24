package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.dpu.test.data.TestDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFDataUnit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.rio.RDFFormat;

import static org.junit.Assert.*;

/**
 * Test funcionality extaction for Virtuoso from SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class SPARQLExtractorVirtuosoTest {

	private final Logger logger = LoggerFactory.getLogger(
			SPARQLExtractorVirtuosoTest.class);

	private static RDFDataUnit repository;

	private static final String URL = "jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2";

	private static final String USER = "dba";

	private static final String PASSWORD = "dba";

	private static final String DEFAULT_GRAPH = "http://default";

	private static final String QUERY_ENDPOINT = "http://localhost:8890/sparql";

	@BeforeClass
	public static void setRDFDataUnit() throws RDFException {
		repository = new VirtuosoRDFDataUnit(URL, 
				USER, PASSWORD, "input", DEFAULT_GRAPH);

	}

	private ExtractorEndpointParams getVirtuosoEndpoint() {
		return new ExtractorEndpointParams();
	}

	@Before
	public void cleanRepository() throws RepositoryException {
        RepositoryConnection connection = repository.getConnection();
        connection.clear(repository.getDataGraph());
	}

	@AfterClass
	public static void deleteRDFDataUnit() {
		((ManagableRdfDataUnit)repository).clear();
		((ManagableRdfDataUnit)repository).release();
	}

	@Test
	public void extractNSoud() throws RepositoryException {
		try {

			RDFFormat usedRDFFormat = RDFFormat.NTRIPLES;

			URL endpointURL = new URL("http://internal.xrg.cz:8890/sparql");
			String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/legislation/nsoud.cz";
			String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o} limit 5000";

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getDataGraph());

			ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
			virtuoso.addDefaultGraph(defaultGraphUri);

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository,
						getTestContext(), virtuoso);
				extractor.extractFromSPARQLEndpoint(endpointURL,
						query, "", "", usedRDFFormat,
						HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE,
						true);

			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = connection.size(repository.getDataGraph());

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	//@Test
	public void extractBigDataFromEndpoint() throws RepositoryException {

		try {
			URL endpointURL = new URL("http://internal.opendata.cz:8890/sparql");
			String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/seznam.gov.cz/ovm/list/notransform";
			String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}";

            RepositoryConnection connection = repository.getConnection();
			long sizeBefore = connection.size(repository.getDataGraph());

			ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
			virtuoso.addDefaultGraph(defaultGraphUri);

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository,
						getTestContext(), virtuoso);
				extractor
						.extractFromSPARQLEndpoint(endpointURL, defaultGraphUri,
						query);

			} catch (RDFException e) {
				fail(e.getMessage());
            }

            long sizeAfter = connection.size(repository.getDataGraph());

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	@Test
	public void extractDataFromSPARQLEndpointTest() throws RepositoryException {

		try {
			URL endpointURL = new URL("http://dbpedia.org/sparql");
			String defaultGraphUri = "http://dbpedia.org";
			String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 50";

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getDataGraph());

			ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
			virtuoso.addDefaultGraph(defaultGraphUri);

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository,
						getTestContext(), virtuoso);

				extractor
						.extractFromSPARQLEndpoint(endpointURL, query);
			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = connection.size(repository.getDataGraph());

			assertTrue(sizeBefore < sizeAfter);

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
			URL endpoint = new URL(QUERY_ENDPOINT.toString());
			String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 10";

			RDFFormat format = RDFFormat.N3;

            RepositoryConnection connection = repository.getConnection();
            long sizeBefore = connection.size(repository.getDataGraph());

			ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository,
						getTestContext(), virtuoso);
				extractor.extractFromSPARQLEndpoint(
						endpoint, query, "", "",
						format);
			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = connection.size(repository.getDataGraph());

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	private DPUContext getTestContext() {
		TestEnvironment environment =  new TestEnvironment();
		return environment.getContext();
	}
}
