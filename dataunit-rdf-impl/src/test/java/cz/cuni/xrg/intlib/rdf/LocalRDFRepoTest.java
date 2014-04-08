package cz.cuni.xrg.intlib.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.model.*;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
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
	protected static ManagableRdfDataUnit rdfRepo;

	/**
	 * Count of thread used in tests.
	 */
	protected static final int THREAD_SIZE = 10;

	/**
	 * Logging info about behavior method.
	 */
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	/**
	 * Basic setting before test execution.
	 */
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

	/**
	 * Test if repository is created.
	 */
	@Test
	public void isRepositoryCreated() {
		assertNotNull(rdfRepo);
	}

	/**
	 * Test adding triple to repository.
	 */
	@Test
	public void addTripleToRepositoryTest1() throws RepositoryException {

		String namespace = "http://school/catedra/";
		String subjectName = "KSI";
		String predicateName = "isResposibleFor";
		String objectName = "Lecture";

        RepositoryConnection connection = rdfRepo.getConnection();
        ValueFactory factory = connection.getValueFactory();
		Resource subject = factory.createURI(namespace + subjectName);
		URI predicate = factory.createURI(namespace + predicateName);
		Value object = factory.createLiteral(objectName);

		testNewTriple(subject, predicate, object, rdfRepo);

	}

	/**
	 * Test adding triple to repository.
	 */
	@Test
	public void addTripleToRepositoryTest2() throws RepositoryException {
		String namespace = "http://human/person/";
		String subjectName = "Jirka";
		String predicateName = "hasFriend";
		String objectName = "Pavel";

        RepositoryConnection connection = rdfRepo.getConnection();
        ValueFactory factory = connection.getValueFactory();
		Resource subject = factory.createURI(namespace + subjectName);
		URI predicate = factory.createURI(namespace + predicateName);
		Value object = factory.createLiteral(objectName);

		testNewTriple(subject, predicate, object, rdfRepo);
	}

	/**
	 * Test adding triple to repository.
	 */
	@Test
	public void addTripleToRepositoryTest3() throws RepositoryException {
		String namespace = "http://namespace/intlib/";
		String subjectName = "subject";
		String predicateName = "object";
		String objectName = "predicate";

        RepositoryConnection connection = rdfRepo.getConnection();
        ValueFactory factory = connection.getValueFactory();
		Resource subject = factory.createURI(namespace + subjectName);
		URI predicate = factory.createURI(namespace + predicateName);
		Value object = factory.createLiteral(objectName);

		testNewTriple(subject, predicate, object, rdfRepo);
	}

	private String getFilePath(String fileName) {
		File file = new File(outDir.toString(), fileName);
		return file.getAbsolutePath();
	}

	/**
	 * Test loading data to RDF/XML file.
	 */
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

	/**
	 * Test loading data to N3 file.
	 */
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

	/**
	 * Test loading data to TRIG file.
	 */
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

	/**
	 * Test loading data to TTL file.
	 */
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

	/**
	 * Testing overwriting file.
	 */
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

	/**
	 * Test extracting data using Statistical handler.
	 */
	@Test
	public void extractUsingStatisticHandler() {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.RDFXML,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data using not existed file.
	 */
	@Test
	public void extractNotExistedFile() {
		File dirFile = new File("NotExistedFile");

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					dirFile.getAbsolutePath(), suffix, baseURI, useSuffix,
					handlerType);
			fail();
		} catch (RDFException e) {
			long newSize = rdfRepo.getTripleCount();
			assertEquals(size, newSize);
		}

	}

	/**
	 * Test extracting data from RDF/XML files.
	 */
	@Test
	public void extract_RDFXML_FilesToRepository() {

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.RDFXML,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from N3 files.
	 */
	@Test
	public void extract_N3_FilesToRepository() {

		String suffix = "shakespeare.n3";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.N3,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from TTL files.
	 */
	@Test
	public void extract_TTL_FilesToRepository() {

		String suffix = ".ttl";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.TURTLE,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from N-TRIPLES files.
	 */
	@Test
	public void extract_NTRIPLES_FilesToRepository() {

		String suffix = ".nt";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.NTRIPLES,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from TRIG files.
	 */
	@Test
	public void extract_TRIG_FilesToRepository() {

		String suffix = ".trig";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.TRIG,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from TRIX files.
	 */
	@Test
	public void extract_TRIX_FilesToRepository() {

		String suffix = ".trix";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, RDFFormat.TRIX,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());

		}

		long newSize = rdfRepo.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test loading data to RDF/XML file.
	 */
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
	 * Test SPARQL transform using SPARQL update query.
	 */
	@Test
	public void transformUsingSPARQLUpdate() throws RepositoryException {

		String namespace = "http://sport/hockey/";
		String subjectName = "Jagr";
		String predicateName = "playes_in";
		String objectName = "Dalas_Stars";

        RepositoryConnection connection = rdfRepo.getConnection();
        ValueFactory factory = connection.getValueFactory();
		Resource subject = factory.createURI(namespace + subjectName);
		URI predicate = factory.createURI(namespace + predicateName);
		Value object = factory.createLiteral(objectName);

		String updateQuery = "DELETE { ?who ?what 'Dalas_Stars' }"
				+ "INSERT { ?who ?what 'Boston_Bruins' } "
				+ "WHERE { ?who ?what 'Dalas_Stars' }";

       connection.add(subject, predicate, object, rdfRepo.getDataGraph());


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
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
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
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
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

	/**
	 * Test running pipeline 'TED' - 2 extraction, 1 transform, 1 load to TTL
	 * file.
	 */
	@Test
	public void TEDPipelineTest() {
		TEDextractFile1ToRepository();
		TEDextractFile2ToRepository();
		TEDTransformSPARQL();
		TEDloadtoTTLFile();

		long addedData = rdfRepo.getTripleCount();

		assertTrue(addedData > 0);
	}

	/**
	 * Test SPARQL transform using SPARQL update query.
	 */
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
			fail(e.getMessage());
		}
	}

	/**
	 * Test if repository is empty.
	 */
	@Test
	public void isRepositoryEmpty() {
		rdfRepo.cleanAllData();
		assertEquals(0, rdfRepo.getTripleCount());
	}

	/**
	 * Delete used repository files for testing.
	 */
	@After
	public void cleanUp() {
		deleteDirectory(pathRepo.toFile());
		deleteDirectory(new File(outDir.toString()));
		rdfRepo.delete();

	}

	private void testNewTriple(Resource subject, URI predicate,
			Value object, ManagableRdfDataUnit repository) {

		long size = repository.getTripleCount();
		boolean isInRepository = repository.isTripleInRepository(
				subject, predicate, object);


        RepositoryConnection connection = null;
        try {
            connection = repository.getConnection();
            connection.add(subject, predicate, object, repository.getDataGraph());
        } catch (RepositoryException e) {
            LOG.error("Error", e);
        }

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
	 * @param directory directory you can delete.
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

	/**
	 * Run 'BIG' pipeline - 3 transformer, 1 loader to N3 file.
	 */
	@Test
	@Category(IntegrationTest.class)
	public void BIGDataTest() {

		//extractBigDataFileToRepository();
		BigTransformQuery1();
		BigTransformQuery2();
		BigTransformQuery3();
		loadBigDataToN3File();
	}

	/**
	 * Testing paralell pipeline running.
	 */
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

						synchronized (localRepository) {
							addParalelTripleToRepository(localRepository);
							extractFromFileToRepository(localRepository);
							transformOverRepository(localRepository);
							loadToFile(localRepository);
						}

						localRepository.delete();

					} catch (IOException ex) {
						throw new RuntimeException(ex);
					} catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }
			});

			task.start();
		}




	}

	/**
	 * Test adding RDF tripes to given repository instance.
	 *
	 * @param repository repository used for adding triples.
	 */
	protected void addParalelTripleToRepository(ManagableRdfDataUnit repository) throws RepositoryException {

		String namespace = "http://school/catedra/";
		String subjectName = "KSI";
		String predicateName = "isResposibleFor";
		String objectName = "Lecture";

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();

		Resource subject = factory.createURI(namespace + subjectName);
		URI predicate = factory.createURI(namespace + predicateName);
		Value object = factory.createLiteral(objectName);

		testNewTriple(subject, predicate, object, repository);
	}

	/**
	 * Test extracting RDF data from given repository instance.
	 *
	 * @param repository repository used for extracting
	 */
	protected void extractFromFileToRepository(ManagableRdfDataUnit repository) {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

		long size = repository.getTripleCount();

		try {
			repository.extractFromFile(
					FileExtractType.PATH_TO_DIRECTORY, null,
					testFileDir, suffix, baseURI, useSuffix, handlerType);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

		long newSize = repository.getTripleCount();

		assertTrue(newSize > size);
	}

	/**
	 * Test transforming SPARQL update query on give repository instance.
	 *
	 * @param repository repository used for transforming
	 */
	protected void transformOverRepository(ManagableRdfDataUnit repository) {
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

	/**
	 * Test loading to file based on give repository instance.
	 *
	 * @param repository repository used for loading
	 */
	protected void loadToFile(ManagableRdfDataUnit repository) {
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
