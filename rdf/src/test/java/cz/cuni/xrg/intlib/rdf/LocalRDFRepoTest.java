package cz.cuni.xrg.intlib.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.impl.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
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
	protected static RDFDataUnit rdfRepo;

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

		rdfRepo = RDFDataUnitFactory.createLocalRDFRepo(pathRepo.toString(),
				"localRepo", "", "http://default");
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

		Resource subject = rdfRepo.createURI(namespace + subjectName);
		URI predicate = rdfRepo.createURI(namespace + predicateName);
		Value object = rdfRepo.createLiteral(objectName);

		testNewTriple(subject, predicate, object, rdfRepo);

	}

	@Test
	public void addTripleToRepositoryTest2() {
		String namespace = "http://human/person/";
		String subjectName = "Jirka";
		String predicateName = "hasFriend";
		String objectName = "Pavel";

		Resource subject = rdfRepo.createURI(namespace + subjectName);
		URI predicate = rdfRepo.createURI(namespace + predicateName);
		Value object = rdfRepo.createLiteral(objectName);

		testNewTriple(subject, predicate, object, rdfRepo);
	}

	@Test
	public void addTripleToRepositoryTest3() {
		String namespace = "http://namespace/intlib/";
		String subjectName = "subject";
		String predicateName = "object";
		String objectName = "predicate";

		Resource subject = rdfRepo.createURI(namespace + subjectName);
		URI predicate = rdfRepo.createURI(namespace + predicateName);
		Value object = rdfRepo.createLiteral(objectName);

		testNewTriple(subject, predicate, object, rdfRepo);
	}

	private String getFilePath(String fileName) {
		File file = new File(outDir.toString(), fileName);
		return file.getAbsolutePath();
	}

	@Test
	public void loadRDFtoXMLFile() {

		String fileName = "RDF_output.rdf";
		RDFFormatType format = RDFFormatType.RDFXML;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
			fail(ex.getMessage());
		}

	}

	@Test
	public void loadRDFtoN3File() {

		String fileName = "N3_output.n3";
		RDFFormatType format = RDFFormatType.N3;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void loadRDFtoTRIGFile() {

		String fileName = "TRIG_output.trig";
		RDFFormatType format = RDFFormatType.TRIG;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void loadRDFtoTURTLEFile() {

		String fileName = "TURTLE_output.ttl";
		RDFFormatType format = RDFFormatType.TTL;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void loadOverWriteFail() {

		String fileName = "CanNotOverWrite_output.rdf";
		RDFFormatType format = RDFFormatType.RDFXML;

		try {
			rdfRepo.loadToFile(getFilePath(fileName),
					format);
			rdfRepo.loadToFile(getFilePath(fileName),
					format);
			fail();

		} catch (CannotOverwriteFileException ex) {
			// test passed
		} catch (RDFException e) {
			fail();
		}
	}

	@Test
	public void extractUsingStatisticHandler() {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = true;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.RDFXML,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void extractNotExistedFile() {
		File dirFile = new File("NotExistedFile");

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = true;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					dirFile.getAbsolutePath(), suffix, baseURI, useSuffix,
					useStatisticHandler);
			fail();
		} catch (RDFException e) {
			long newSize = rdfRepo.getTripleCount();
			assertEquals(size, newSize);
		}

	}

	@Test
	public void extract_RDFXML_FilesToRepository() {

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.RDFXML,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void extract_N3_FilesToRepository() {

		String suffix = "shakespeare.n3";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.N3,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void extract_TTL_FilesToRepository() {

		String suffix = ".ttl";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.TURTLE,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void extract_NTRIPLES_FilesToRepository() {

		String suffix = ".nt";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.NTRIPLES,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void extract_TRIG_FilesToRepository() {

		String suffix = ".trig";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.TRIG,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void extract_TRIX_FilesToRepository() {

		String suffix = ".trix";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.TRIX,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	@Test
	public void loadAllToXMLfile() {

		String fileName = "AllData_output.rdf";
		RDFFormatType format = RDFFormatType.RDFXML;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
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

			long sizeBefore = rdfRepo.getTripleCount();

			try {
				rdfRepo.addFromSPARQLEndpoint(endpointURL, defaultGraphUri,
						query);
			} catch (RDFException e) {
				fail(e.getMessage());
			}

			long sizeAfter = rdfRepo.getTripleCount();

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
//	@Category(IntegrationTest.class)
//	public void extractDataFromSPARQLEndpointNamePasswordTest() {
//		try {
//			URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
//			String defaultGraphUri = "";
//			String query = "select * where {?s ?o ?p} LIMIT 10";
//			String name = "SPARQL";
//			String password = "nejlepsipaper";
//
//			RDFFormat format = RDFFormat.N3;
//
//			long sizeBefore = rdfRepo.getTripleCount();
//
//			try {
//				rdfRepo.extractFromSPARQLEndpoint(
//						endpointURL, defaultGraphUri, query, name, password,
//						format);
//			} catch (RDFException e) {
//				fail(e.getMessage());
//			}
//
//			long sizeAfter = rdfRepo.getTripleCount();
//
//			assertTrue(sizeBefore < sizeAfter);
//
//		} catch (MalformedURLException ex) {
//			LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
//		}
//	}
	/**
	 * This is not unit test, as it depends on remote server -> commented out
	 * for build, use only when debugging
	 */
////    @Test
//	@Category(IntegrationTest.class)
//	public void loadDataToSPARQLEndpointTest() {
//		try {
//			URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
//			String defaultGraphUri = "http://ld.opendata.cz/resource/myGraph/001";
//			String name = "SPARQL";
//			String password = "nejlepsipaper";
//			WriteGraphType graphType = WriteGraphType.MERGE;
//			InsertType insertType = InsertType.SKIP_BAD_PARTS;
//
//			try {
//				rdfRepo.loadtoSPARQLEndpoint(endpointURL, defaultGraphUri, name,
//						password, graphType, insertType);
//			} catch (RDFException e) {
//				fail(e.getMessage());
//			}
//
//
//		} catch (MalformedURLException ex) {
//			LOG.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
//		}
//
//	}

	/*TEST TO DO !!!*/ @Test
	public void transformUsingSPARQLUpdate() {

		String namespace = "http://sport/hockey/";
		String subjectName = "Jagr";
		String predicateName = "playes_in";
		String objectName = "Dalas_Stars";

		Resource subject = rdfRepo.createURI(namespace + subjectName);
		URI predicate = rdfRepo.createURI(namespace + predicateName);
		Value object = rdfRepo.createLiteral(objectName);

		String updateQuery = "DELETE { ?who ?what 'Dalas_Stars' }"
				+ "INSERT { ?who ?what 'Boston_Bruins' } "
				+ "WHERE { ?who ?what 'Dalas_Stars' }";

		rdfRepo.addTriple(subject, predicate, object);

		boolean beforeUpdate = rdfRepo.isTripleInRepository(
				subject, predicate, object);
		assertTrue(beforeUpdate);

		try {
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		boolean afterUpdate = rdfRepo.isTripleInRepository(
				subject, predicate, object);
		assertFalse(afterUpdate);
	}

	private void TEDextractFile1ToRepository() {

		String suffix = "ted4.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	private void TEDextractFile2ToRepository() {

		String suffix = "ted4b.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

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
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
			//*VIRTUOSO TODO !!! */ fail(e.getMessage());
		}

	}

	private void TEDloadtoTTLFile() {

		String fileName = "output-ted-test.ttl";
		RDFFormatType format = RDFFormatType.TTL;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void TEDPipelineTest() {
		TEDextractFile1ToRepository();
		TEDextractFile2ToRepository();
		TEDTransformSPARQL();
		TEDloadtoTTLFile();

		long addedData = rdfRepo.getTripleCount();

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
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
			//*VIRTUOSO*/fail(e.getMessage());
		}
	}

	@Test
	public void isRepositoryEmpty() {
		rdfRepo.cleanAllData();
		assertEquals(0, rdfRepo.getTripleCount());
	}

	@After
	public void cleanUp() {
		deleteDirectory(pathRepo.toFile());
		deleteDirectory(new File(outDir.toString()));
		rdfRepo.release();

	}

	private void testNewTriple(Resource subject, URI predicate,
			Value object, RDFDataUnit repository) {

		long size = repository.getTripleCount();
		boolean isInRepository = repository.isTripleInRepository(
				subject, predicate, object);

		repository.addTriple(subject, predicate, object);
		long expectedSize = repository.getTripleCount();

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

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(FileExtractType.PATH_TO_FILE, null,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

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
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
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
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
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
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		LOG.debug("Transform Query 3 - OK");
	}

	private void loadBigDataToN3File() {

		String fileName = "BIG_Data.n3";
		RDFFormatType format = RDFFormatType.N3;
		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			rdfRepo.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
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

						LocalRDFRepo localRepository = RDFDataUnitFactory
								.createLocalRDFRepo(path
								.toString(), "local", "", "http://default");

						addParalelTripleToRepository(localRepository);
						extractFromFileToRepository(localRepository);
						transformOverRepository(localRepository);
						loadToFile(localRepository);

						localRepository.delete();

					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});

			task.start();
		}




	}

	protected void addParalelTripleToRepository(RDFDataUnit repository) {

		String namespace = "http://school/catedra/";
		String subjectName = "KSI";
		String predicateName = "isResposibleFor";
		String objectName = "Lecture";

		Resource subject = rdfRepo.createURI(namespace + subjectName);
		URI predicate = rdfRepo.createURI(namespace + predicateName);
		Value object = rdfRepo.createLiteral(objectName);

		testNewTriple(subject, predicate, object, repository);
	}

	protected void extractFromFileToRepository(RDFDataUnit repository) {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		boolean useStatisticHandler = false;

		long size = repository.getTripleCount();

		try {
			repository.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY,null,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = repository.getTripleCount();

		assertTrue(newSize > size);
	}

	protected void transformOverRepository(RDFDataUnit repository) {
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
			repository.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
			fail(e.getMessage());
		}
	}

	protected void loadToFile(RDFDataUnit repository) {
		String fileName = "TTL_output.ttl";
		RDFFormatType format = RDFFormatType.TTL;

		boolean canBeOverWriten = true;
		boolean isNameUnique = false;

		try {
			repository.loadToFile(
					getFilePath(fileName), format, canBeOverWriten,
					isNameUnique);

		} catch (CannotOverwriteFileException | RDFException ex) {
			fail(ex.getMessage());
		}
	}
}
