package cz.cuni.xrg.intlib.rdf;

import cz.cuni.xrg.intlib.commons.IntegrationTest;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;

import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.*;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.*;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRDFRepoTest {

	/**
	 * Path to test repository
	 */
	private Path pathRepo;

	/**
	 * Path to directory with produced data
	 */
	protected Path outDir;

	/**
	 * Path to directory with test input data
	 */
	protected String testFileDir;

	/**
	 * Local repository
	 */
	protected static RDFDataRepository rdfRepo;

	protected static final int THREAD_SIZE = 10;

	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() {
		try {
			pathRepo = Files.createTempDirectory("intlib-repo");
			outDir = Files.createTempDirectory("intlib-out");
			testFileDir = LocalRDFRepoTest.class.getResource("/repository")
					.getPath();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		rdfRepo = LocalRDFRepo.createLocalRepo(pathRepo.toString(), "localRepo");
	}

	@Test
	public void isRepositoryCreated() {
		assertNotNull(rdfRepo);
	}

	@Test
	public void addTripleToRepositoryTest1() {

		String namespace = "http://school/catedra/";
		String subjectName = "KSI";
		String predicateName = "isResposibleFor";
		String objectName = "Lecture";

		testNewTriple(namespace, subjectName, predicateName, objectName, rdfRepo);

	}

	@Test
	public void addTripleToRepositoryTest2() {
		String namespace = "http://human/person/";
		String subjectName = "Jirka";
		String predicateName = "hasFriend";
		String objectName = "Pavel";

		testNewTriple(namespace, subjectName, predicateName, objectName, rdfRepo);
	}

	@Test
	public void addTripleToRepositoryTest3() {
		String namespace = "http://namespace/intlib/";
		String subjectName = "subject";
		String predicateName = "object";
		String objectName = "predicate";

		testNewTriple(namespace, subjectName, predicateName, objectName, rdfRepo);
	}

	@Test
	public void loadRDFtoXMLFile() {

		String fileName = "RDF_output.rdf";
		RDFFormat format = RDFFormat.RDFXML;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}

	}

	@Test
	public void loadRDFtoN3File() {

		String fileName = "N3_output.n3";
		RDFFormat format = RDFFormat.N3;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void loadRDFtoTRIGFile() {

		String fileName = "TRIG_output.trig";
		RDFFormat format = RDFFormat.TRIG;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void loadRDFtoTURTLEFile() {

		String fileName = "TURTLE_output.ttl";
		RDFFormat format = RDFFormat.TURTLE;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void loadOverWriteFail() {

		String fileName = "CanNotOverWrite_output.rdf";
		RDFFormat format = RDFFormat.RDFXML;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(outDir.toString(), fileName,
					format);
			rdfRepo.loadRDFfromRepositoryToFile(outDir.toString(), fileName,
					format);
			fail();

		} catch (CannotOverwriteFileException ex) {
			// test passed
		} catch (LoadException e) {
			fail();
		}
	}

	@Test
	public void extractUsingStatisticHandler()
	{
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = true;

		long size = rdfRepo.getTripleCountInRepository();

		try {
			rdfRepo.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCountInRepository();

		assertTrue(newSize > size);
	}
	@Test
	public void extractRDFFilesToRepository() {

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCountInRepository();

		try {
			rdfRepo.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCountInRepository();

		assertTrue(newSize > size);
	}

	@Test
	public void extractN3FilesToRepository() {

		String suffix = ".n3";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCountInRepository();

		try {
			rdfRepo.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCountInRepository();

		assertTrue(newSize > size);
	}

	@Test
	public void loadAllToXMLfile() {

		String fileName = "AllData_output.rdf";
		RDFFormat format = RDFFormat.RDFXML;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * This is not unit test, as it depends on remote server -> commented out
	 * for build, use only when debugging
	 */
	@Test
	@Category(IntegrationTest.class)
	public void extractDataFromSPARQLEndpointTest() {

		try {
			URL endpointURL = new URL("http://dbpedia.org/sparql");
			String defaultGraphUri = "http://dbpedia.org";
			String query = "select * where {?s ?o ?p} LIMIT 50";

			long sizeBefore = rdfRepo.getTripleCountInRepository();

			try {
				rdfRepo.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri,
						query);
			} catch (ExtractException e) {
				fail(e.getMessage());
			}

			long sizeAfter = rdfRepo.getTripleCountInRepository();

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	/**
	 * This is not unit test, as it depends on remote server -> commented out
	 * for build, use only when debugging
	 */
//    @Test
	@Category(IntegrationTest.class)
	public void extractDataFromSPARQLEndpointNamePasswordTest() {
		try {
			URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
			String defaultGraphUri = "";
			String query = "select * where {?s ?o ?p} LIMIT 10";
			String name = "SPARQL";
			String password = "nejlepsipaper";

			RDFFormat format = RDFFormat.N3;

			long sizeBefore = rdfRepo.getTripleCountInRepository();

			try {
				rdfRepo.extractfromSPARQLEndpoint(
						endpointURL, defaultGraphUri, query, name, password,
						format);
			} catch (ExtractException e) {
				fail(e.getMessage());
			}

			long sizeAfter = rdfRepo.getTripleCountInRepository();

			assertTrue(sizeBefore < sizeAfter);

		} catch (MalformedURLException ex) {
			LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	/**
	 * This is not unit test, as it depends on remote server -> commented out
	 * for build, use only when debugging
	 */
//    @Test
	@Category(IntegrationTest.class)
	public void loadDataToSPARQLEndpointTest() {
		try {
			URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
			String defaultGraphUri = "http://ld.opendata.cz/resource/myGraph/001";
			String name = "SPARQL";
			String password = "nejlepsipaper";
			WriteGraphType graphType = WriteGraphType.MERGE;

			try {
				rdfRepo.loadtoSPARQLEndpoint(endpointURL, defaultGraphUri, name,
						password, graphType);
			} catch (LoadException e) {
				fail(e.getMessage());
			}


		} catch (MalformedURLException ex) {
			LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}

	}

	/*TEST TO DO !!!*/ @Test
	public void transformUsingSPARQLUpdate() {

		String namespace = "http://sport/hockey/";
		String subjectName = "Jagr";
		String predicateName = "playes_in";
		String objectName = "Dalas_Stars";

		String updateQuery = "DELETE { ?who ?what 'Dalas_Stars' }"
				+ "INSERT { ?who ?what 'Boston_Bruins' } "
				+ "WHERE { ?who ?what 'Dalas_Stars' }";

		rdfRepo.addTripleToRepository(
				namespace, subjectName, predicateName, objectName);

		boolean beforeUpdate = rdfRepo.isTripleInRepository(
				namespace, subjectName, predicateName, objectName);
		assertTrue(beforeUpdate);

		try {
			rdfRepo.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			fail(e.getMessage());
		}

		boolean afterUpdate = rdfRepo.isTripleInRepository(
				namespace, subjectName, predicateName, objectName);
		assertFalse(afterUpdate);
	}

	private void TEDextractFile1ToRepository() {

		String suffix = "ted4.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCountInRepository();

		try {
			rdfRepo.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCountInRepository();

		assertTrue(newSize > size);
	}

	private void TEDextractFile2ToRepository() {

		String suffix = "ted4b.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCountInRepository();

		try {
			rdfRepo.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCountInRepository();

		boolean triplesAdded = newSize > size;

		assertTrue(triplesAdded);
	}

	private void TEDTransformSPARQL() {

		String updateQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
				+ "INSERT DATA"
				+ "{ "
				+ "<http://example/book1> dc:title \"A new book\" ."
				+ "}";

		try {
			rdfRepo.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			//*VIRTUOSO TODO !!! */ fail(e.getMessage());
		}

	}

	private void TEDloadtoTTLFile() {

		String fileName = "output-ted-test.ttl";
		RDFFormat format = RDFFormat.TURTLE;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void TEDPipelineTest() {
		TEDextractFile1ToRepository();
		TEDextractFile2ToRepository();
		TEDTransformSPARQL();
		TEDloadtoTTLFile();

		long addedData = rdfRepo.getTripleCountInRepository();

		assertTrue(addedData > 0);
	}

	@Test
	public void SecondUpdateQueryTest() {

		String updateQuery = "prefix s: <http://schema.org/> "
				+ "DELETE {?s s:streetAddress ?o} "
				+ "INSERT {?s s:streetAddress ?x} "
				+ "WHERE {"
				+ "{ SELECT ?s ?o ?x "
				+ "WHERE {?s s:streetAddress ?o}} FILTER (BOUND(?x))}";

		try {
			rdfRepo.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			//*VIRTUOSO*/fail(e.getMessage());
		}
	}

	@Test
	public void isRepositoryEmpty() {
		rdfRepo.cleanAllRepositoryData();
		assertEquals(0, rdfRepo.getTripleCountInRepository());
	}

	@After
	public void cleanUp() {
		deleteDirectory(pathRepo.toFile());
		deleteDirectory(new File(outDir.toString()));
		rdfRepo.release();

	}

	private void testNewTriple(String namespace,
			String subjectName,
			String predicateName,
			String objectName, RDFDataRepository repository) {

		long size = repository.getTripleCountInRepository();
		boolean isInRepository = repository.isTripleInRepository(
				namespace, subjectName, predicateName, objectName);

		repository.addTripleToRepository(
				namespace, subjectName, predicateName, objectName);
		long expectedSize = repository.getTripleCountInRepository();

		if (isInRepository) {
			assertEquals(expectedSize, size);
		} else {
			assertEquals(expectedSize, size + 1L);
		}
	}

	/**
	 * Recursively deletes a directory, follows symbolic links
	 *
	 * @param directory
	 */
	protected static void deleteDirectory(File directory) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		directory.delete();
	}

	private void extractBigDataFileToRepository() {

		String suffix = "bigdata.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = true;

		long size = rdfRepo.getTripleCountInRepository();

		try {
			rdfRepo.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCountInRepository();

		LOG.debug("EXTRACTING from FILE - OK");
		LOG.debug(
				"EXTRACT TOTAL: " + String.valueOf(newSize - size) + " triples.");

	}

	private void BigTransformQuery1() {

		// Dotaz nahrazuje vsechny objekty jejich spravnymi URI

		String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
				+ "PREFIX adms: <http://www.w3.org/ns/adms#> "
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "DELETE { "
				+ "  ?s ?p ?s1 . } "
				+ "INSERT { "
				+ "  ?s ?p ?s2 . } "
				+ "WHERE { "
				+ "  ?s1 owl:sameAs ?s2 . "
				+ "  ?s ?p ?s1 . }";

		try {
			rdfRepo.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			fail(e.getMessage());
		}

		LOG.debug("Transform Query 1 - OK");
	}

	private void BigTransformQuery2() {

		// Dotaz nahrazuje vsechny subjekty jejich spravnymi URI

		String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
				+ "PREFIX adms: <http://www.w3.org/ns/adms#> "
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "DELETE { "
				+ "  ?s1 ?p ?o . } "
				+ "INSERT { "
				+ "  ?s2 ?p ?o . } "
				+ "WHERE { "
				+ "  ?s1 owl:sameAs ?s2 . "
				+ "  ?s ?p ?o . }";

		try {
			rdfRepo.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			fail(e.getMessage());
		}

		LOG.debug("Transform Query 2 - OK");
	}

	private void BigTransformQuery3() {

		//Maze same-as na spatne URI

		String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
				+ "PREFIX adms: <http://www.w3.org/ns/adms#> "
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "DELETE { "
				+ "  ?s1 owl:sameAs ?s2 . } "
				+ "WHERE { "
				+ "  ?s1 owl:sameAs ?s2 . }";


		try {
			rdfRepo.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			fail(e.getMessage());
		}

		LOG.debug("Transform Query 3 - OK");
	}

	private void loadBigDataToN3File() {

		String fileName = "BIG_Data.n3";
		RDFFormat format = RDFFormat.N3;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}

		LOG.debug("LOADING from FILE - OK");
	}

	@Test
	@Category(IntegrationTest.class)
	public void BIGDataTest() {

		//extractBigDataFileToRepository();
		BigTransformQuery1();
		BigTransformQuery2();
		BigTransformQuery3();
		loadBigDataToN3File();
	}

	@Test
	public void paralellPipelineRunning() {

		for (int i = 0; i < THREAD_SIZE; i++) {

			Thread task = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Path path = Files.createTempDirectory("directory");

						LocalRDFRepo localRepository = LocalRDFRepo
								.createLocalRepo(path
								.toString(), "local");

						addParalelTripleToRepository(localRepository);
						extractFromFileToRepository(localRepository);
						transformOverRepository(localRepository);
						loadToFile(localRepository);

						localRepository.release();

					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});

			task.start();
		}




	}

	protected void addParalelTripleToRepository(RDFDataRepository repository) {

		String namespace = "http://school/catedra/";
		String subjectName = "KSI";
		String predicateName = "isResposibleFor";
		String objectName = "Lecture";

		testNewTriple(namespace, subjectName, predicateName, objectName,
				repository);
	}

	protected void extractFromFileToRepository(RDFDataRepository repository) {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = repository.getTripleCountInRepository();

		try {
			repository.extractRDFfromFileToRepository(
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (ExtractException e) {
			fail(e.getMessage());
		}

		long newSize = repository.getTripleCountInRepository();

		assertTrue(newSize > size);
	}

	protected void transformOverRepository(RDFDataRepository repository) {
		String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
				+ "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
				+ "PREFIX adms: <http://www.w3.org/ns/adms#> "
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "DELETE { "
				+ "  ?s1 ?p ?o . } "
				+ "INSERT { "
				+ "  ?s2 ?p ?o . } "
				+ "WHERE { "
				+ "  ?s1 owl:sameAs ?s2 . "
				+ "  ?s ?p ?o . }";

		try {
			repository.transformUsingSPARQL(updateQuery);
		} catch (TransformException e) {
			fail(e.getMessage());
		}
	}

	protected void loadToFile(RDFDataRepository repository) {
		String fileName = "TTL_output.ttl";
		RDFFormat format = RDFFormat.TURTLE;

		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			repository.loadRDFfromRepositoryToFile(
					outDir.toString(), fileName, format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | LoadException ex) {
			fail(ex.getMessage());
		}
	}
}
