package cz.cuni.xrg.intlib.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.impl.SPARQLoader;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import static org.junit.Assert.*;

/**
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class VirtuosoTest extends LocalRDFRepoTest {

	private static final String hostName = "localhost";

	private static final String port = "1111";

	private static final String user = "dba";

	private static final String password = "dba";

	private static final String defaultGraph = "http://default";

	private static final String updateEndpoint = "http://localhost:8890/sparql-auth";

	@BeforeClass
	public static void setUpLogger() {

		rdfRepo = RDFDataUnitFactory.createVirtuosoRDFRepo(
				hostName, port, user, password, defaultGraph, "",
				new Properties());
		rdfRepo.cleanAllData();
	}

	@Override
	public void setUp() {
		try {
			outDir = Files.createTempDirectory("intlib-out");
			testFileDir = VirtuosoTest.class.getResource("/repository")
					.getPath();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());

		}
	}

	@Override
	public void cleanUp() {
		deleteDirectory(new File(outDir.toString()));
	}

	@AfterClass
	public static void cleaning() {
		rdfRepo.delete();
	}

	@Test
	public void addDataUsingTransformer() {
		String query = "insert data {<http://test>  <http://test>  <http://test> .}";
		try {
			rdfRepo.executeSPARQLUpdateQuery(query);
		} catch (RDFException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	@Override
	public void BIGDataTest() {
		super.BIGDataTest();
	}

	@Test
	public void BIGTwoGigaFileExtraction() {
		//extractTwoGigaFile();
	}

	@Test
	public void repositoryCopy() {
		RDFDataUnit goal = RDFDataUnitFactory.createVirtuosoRDFRepo(
				hostName, port, user, password, "http://goal", "",
				new Properties());
		try {
			goal.merge(rdfRepo);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private void extractTwoGigaFile() {
		String suffix = "ted1.n3";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(FileExtractType.PATH_TO_FILE, null,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

		LOG.debug("EXTRACTING from FILE - OK");
		LOG.debug(
				"EXTRACT TOTAL: " + String.valueOf(newSize - size) + " triples.");
	}

	@Test
	@Override
	public void paralellPipelineRunning() {

		for (int i = 0; i < THREAD_SIZE; i++) {

			final String namedGraph = "http://myDefault" + i;
			Thread task = new Thread(new Runnable() {
				@Override
				public void run() {

					VirtuosoRDFRepo virtuosoRepo = RDFDataUnitFactory
							.createVirtuosoRDFRepo(
							hostName,
							port,
							user,
							password,
							namedGraph,
							"",
							new Properties());

					synchronized (virtuosoRepo) {
						addParalelTripleToRepository(virtuosoRepo);
						extractFromFileToRepository(virtuosoRepo);
						transformOverRepository(virtuosoRepo);
						loadToFile(virtuosoRepo);
					}
					virtuosoRepo.delete();


				}
			});

			task.start();
		}
	}

	@Test
	public void InsertingToEndpointTest1() {
		rdfRepo.cleanAllData();

		Resource subject = rdfRepo.createURI("http://my.subject");
		URI predicate = rdfRepo.createURI("http://my.predicate");
		Value object = rdfRepo.createLiteral("My company s.r.o. \"HOME\"");

		tryInsertToSPARQLEndpoint(subject, predicate, object);
	}

	//t
	@Test
	public void InsertingToEndpointTest2() {
		rdfRepo.cleanAllData();

		Resource subject = rdfRepo.createURI("http://my.subject");
		URI predicate = rdfRepo.createURI("http://my.predicate");
		Value object = rdfRepo.createLiteral(
				"This \"firma has 'firma' company\" Prague");

		tryInsertToSPARQLEndpoint(subject, predicate, object);
	}

	@Test
	public void InsertingToEndpointTest3() {
		rdfRepo.cleanAllData();

		Resource subject = rdfRepo.createURI("http://my.subject");
		URI predicate = rdfRepo.createURI("http://my.predicate");
		Value object = rdfRepo.createLiteral(
				"Test char <and > in <my text1> as example.");

		tryInsertToSPARQLEndpoint(subject, predicate, object);
	}

	private void tryInsertToSPARQLEndpoint(Resource subject, URI predicate,
			Value object) {

		rdfRepo.addTriple(subject, predicate, object);

		String goalGraphName = "http://temp";
		URL endpoint = getUpdateEndpoint();

		boolean isLoaded = false;

		try {
			SPARQLoader loader = new SPARQLoader(rdfRepo);
			loader.loadToSPARQLEndpoint(endpoint, goalGraphName, user,
					password,
					WriteGraphType.OVERRIDE, InsertType.SKIP_BAD_PARTS);
			isLoaded = true;

		} catch (RDFException e) {
			LOG.error("INSERT triple: {} {} {} to SPARQL endpoint failed",
					subject.stringValue(),
					predicate.stringValue(),
					object.stringValue());

		} finally {
			try {
				rdfRepo.clearEndpointGraph(endpoint, goalGraphName);
			} catch (RDFException e) {
				LOG.error("TEMP graph <" + goalGraphName + "> was not delete");
			}
		}

		assertTrue(isLoaded);
	}

	private URL getUpdateEndpoint() {

		URL endpoint = null;

		try {
			endpoint = new URL(updateEndpoint);

		} catch (MalformedURLException e) {
			LOG.debug("Malformed URL to SPARQL update endpoint " + e
					.getMessage());

		} finally {
			return endpoint;
		}

	}
}
