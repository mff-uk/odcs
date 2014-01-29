package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 * Test funcionality loading to SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class SPARQLLoaderTest {

	private final Logger logger = LoggerFactory.getLogger(
			SPARQLLoaderTest.class);

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

	@AfterClass
	public static void deleteRDFDataUnit() {
		repository.delete();
	}

	@Test
	public void InsertingToEndpointTest1() {
		repository.cleanAllData();

		Resource subject = repository.createURI("http://my.subject");
		URI predicate = repository.createURI("http://my.predicate");
		Value object = repository.createLiteral("My company s.r.o. \"HOME\"");

		tryInsertToSPARQLEndpoint(subject, predicate, object);
	}

	@Test
	public void InsertingToEndpointTest2() {
		repository.cleanAllData();

		Resource subject = repository.createURI("http://my.subject");
		URI predicate = repository.createURI("http://my.predicate");
		Value object = repository.createLiteral(
				"This \"firma has 'firma' company\" Prague");

		tryInsertToSPARQLEndpoint(subject, predicate, object);
	}

	@Test
	public void InsertingToEndpointTest3() {
		repository.cleanAllData();

		Resource subject = repository.createURI("http://my.subject");
		URI predicate = repository.createURI("http://my.predicate");
		Value object = repository.createLiteral(
				"Test char <and > in <my text1> as example.");

		tryInsertToSPARQLEndpoint(subject, predicate, object);
	}

	@Test
	public void loadDataToSPARQLEndpointTest() {
		try {
			URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
			String defaultGraphUri = "http://ld.opendata.cz/resource/myGraph/001";
			String name = "SPARQL";
			String password = "nejlepsipaper";
			WriteGraphType graphType = WriteGraphType.MERGE;
			InsertType insertType = InsertType.SKIP_BAD_PARTS;

			try {
				SPARQLoader loader = new SPARQLoader(repository,
						getTestContext());

				loader.loadToSPARQLEndpoint(endpointURL, defaultGraphUri, name,
						password, graphType, insertType);
			} catch (RDFException e) {
				fail(e.getMessage());
			}


		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}

	}

	private void tryInsertToSPARQLEndpoint(Resource subject, URI predicate,
			Value object) {

		repository.addTriple(subject, predicate, object);

		String goalGraphName = "http://tempGraph";
		URL endpoint = getUpdateEndpoint();

		boolean isLoaded = false;

		try {
			SPARQLoader loader = new SPARQLoader(repository, getTestContext());

			loader.loadToSPARQLEndpoint(endpoint, goalGraphName, USER,
					PASSWORD,
					WriteGraphType.OVERRIDE, InsertType.SKIP_BAD_PARTS);
			isLoaded = true;

		} catch (RDFException e) {
			logger.error("INSERT triple: {} {} {} to SPARQL endpoint failed",
					subject.stringValue(),
					predicate.stringValue(),
					object.stringValue());

		} finally {
			try {
				repository.clearEndpointGraph(endpoint, goalGraphName,
						getTestContext());
			} catch (RDFException e) {
				logger.error(
						"TEMP graph <" + goalGraphName + "> was not delete");
			}
		}

		assertTrue(isLoaded);
	}

	private DPUContext getTestContext() {
		TestEnvironment environment = TestEnvironment.create();
		return environment.getContext();
	}

	private URL getUpdateEndpoint() {

		URL endpoint = null;

		try {
			endpoint = new URL(UPDATE_ENDPOINT);

		} catch (MalformedURLException e) {
			logger.debug("Malformed URL to SPARQL update endpoint " + e
					.getMessage());

		} finally {
			return endpoint;
		}

	}
}
