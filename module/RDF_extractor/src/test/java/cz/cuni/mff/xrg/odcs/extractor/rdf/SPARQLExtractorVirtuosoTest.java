package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
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

	private static final String HOST_NAME = "localhost";

	private static final String PORT = "1111";

	private static final String USER = "dba";

	private static final String PASSWORD = "dba";

	private static final String DEFAULT_GRAPH = "http://default";

	private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-auth";

	@BeforeClass
	public static void setRDFDataUnit() throws RDFException {

		repository = RDFDataUnitFactory.createVirtuosoRDFRepo(HOST_NAME, PORT,
				USER, PASSWORD, DEFAULT_GRAPH, "input", new Properties());

	}

	@Before
	public void cleanRepository() {
		repository.cleanAllData();
	}

	@AfterClass
	public static void deleteRDFDataUnit() {
		repository.delete();
	}

	@Test
	public void extractBigDataFromEndpoint() {

		try {
			URL endpointURL = new URL("http://internal.opendata.cz:8890/sparql");
			String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/seznam.gov.cz/ovm/list/notransform";
			String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}";

			long sizeBefore = repository.getTripleCount();

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository);
				extractor
						.extractFromSPARQLEndpoint(endpointURL, defaultGraphUri,
						query);

			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = repository.getTripleCount();

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	@Test
	public void extractDataFromSPARQLEndpointTest() {

		try {
			URL endpointURL = new URL("http://dbpedia.org/sparql");
			String defaultGraphUri = "http://dbpedia.org";
			String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 50";

			long sizeBefore = repository.getTripleCount();

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository);

				extractor
						.extractFromSPARQLEndpoint(endpointURL, defaultGraphUri,
						query);
			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = repository.getTripleCount();

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
	public void extractDataFromSPARQLEndpointNamePasswordTest() {
		try {
			URL endpoint = new URL(UPDATE_ENDPOINT.toString());
			String defaultGraphUri = "";
			String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 10";

			RDFFormat format = RDFFormat.N3;

			long sizeBefore = repository.getTripleCount();

			try {
				SPARQLExtractor extractor = new SPARQLExtractor(repository);
				extractor.extractFromSPARQLEndpoint(
						endpoint, defaultGraphUri, query, USER, PASSWORD,
						format);
			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = repository.getTripleCount();

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}
}
