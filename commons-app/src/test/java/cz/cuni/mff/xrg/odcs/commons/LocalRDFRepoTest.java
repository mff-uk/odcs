package cz.cuni.mff.xrg.odcs.commons;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.LocalRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;

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

    final private String encode = "UTF-8";

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

		rdfRepo = new LocalRDFDataUnit(pathRepo.toString(),
				"myTestName", "http://default");
	}

	/**
	 * Test if repository is created.
	 */
	@Test
	public void isRepositoryCreated() {
		assertNotNull(rdfRepo);
	}

    private void load(String fileName) throws FileNotFoundException, RepositoryException, RDFHandlerException {
        FileOutputStream out = new FileOutputStream(getFilePath(fileName));
        OutputStreamWriter os = new OutputStreamWriter(out, Charset.forName(encode));
        RDFWriter rdfWriter = Rio.createWriter(Rio.getWriterFormatForFileName(fileName), os);
        RepositoryConnection connection = rdfRepo.getConnection();
        connection.export(rdfWriter, rdfRepo.getDataGraph());
        connection.close();

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

		try {
            load(fileName);
		} catch ( Exception ex) {
			fail(ex.getMessage());
		}

	}


	/**
	 * Test extracting data using Statistical handler.
	 */
	@Test
	public void extractUsingStatisticHandler() throws RepositoryException {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

		long newSize = connection.size(rdfRepo.getDataGraph());

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data using not existed file.
	 */
	@Test
	public void extractNotExistedFile() throws RepositoryException {
		File dirFile = new File("NotExistedFile");

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());
        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

	}

	/**
	 * Test extracting data from RDF/XML files.
	 */
	@Test
	public void extract_RDFXML_FilesToRepository() throws RepositoryException {

		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


        long newSize = connection.size(rdfRepo.getDataGraph());

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from N3 files.
	 */
    @Test
    public void extract_N3_FilesToRepository() throws RepositoryException {
        String suffix = "shakespeare.n3";
        String baseURI = "";
        boolean useSuffix = true;
        HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;
        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());
        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        long newSize = connection.size(rdfRepo.getDataGraph());
        assertTrue(newSize > size);
    }

	/**
	 * Test extracting data from TTL files.
	 */
	@Test
	public void extract_TTL_FilesToRepository() throws RepositoryException {

		String suffix = ".ttl";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

		long newSize =  connection.size(rdfRepo.getDataGraph());

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from N-TRIPLES files.
	 */
	@Test
	public void extract_NTRIPLES_FilesToRepository() throws RepositoryException {

		String suffix = ".nt";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

		long newSize = connection.size(rdfRepo.getDataGraph());
		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from TRIG files.
	 */
	@Test
	public void extract_TRIG_FilesToRepository() throws RepositoryException {

		String suffix = ".trig";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


        long newSize = connection.size(rdfRepo.getDataGraph());

		assertTrue(newSize > size);
	}

	/**
	 * Test extracting data from TRIX files.
	 */
	@Test
	public void extract_TRIX_FilesToRepository() throws RepositoryException {

		String suffix = ".trix";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


        long newSize =  connection.size(rdfRepo.getDataGraph());

		assertTrue(newSize > size);
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

		boolean beforeUpdate = connection.hasStatement(subject, predicate, object, true, rdfRepo.getDataGraph());
		assertTrue(beforeUpdate);

		try {
			rdfRepo.executeSPARQLUpdateQuery(updateQuery);
		} catch (RDFException e) {
			fail(e.getMessage());
		}

        boolean afterUpdate = connection.hasStatement(subject, predicate, object, true, rdfRepo.getDataGraph());
        assertFalse(afterUpdate);
	}

	private void TEDextractFile1ToRepository() throws RepositoryException {

		String suffix = "ted4.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
		long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

		long newSize =  connection.size(rdfRepo.getDataGraph());

		assertTrue(newSize > size);
	}

	private void TEDextractFile2ToRepository() throws RepositoryException {

		String suffix = "ted4b.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;


        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

		long newSize = connection.size(rdfRepo.getDataGraph());

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

	/**
	 * Test running pipeline 'TED' - 2 extraction, 1 transform, 1 load to TTL
	 * file.
	 */
	@Test
	public void TEDPipelineTest() throws RepositoryException {
		TEDextractFile1ToRepository();
		TEDextractFile2ToRepository();
		TEDTransformSPARQL();

        RepositoryConnection connection = rdfRepo.getConnection();
		long addedData = connection.size(rdfRepo.getDataGraph());

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
	public void isRepositoryEmpty() throws RepositoryException {
        RepositoryConnection connection = rdfRepo.getConnection();
        connection.clear(rdfRepo.getDataGraph());
		assertEquals(0, connection.size(rdfRepo.getDataGraph()));
	}

	/**
	 * Delete used repository files for testing.
	 */
	@After
	public void cleanUp() {
		deleteDirectory(pathRepo.toFile());
		deleteDirectory(new File(outDir.toString()));
		rdfRepo.clear();
		rdfRepo.release();

	}

	private void testNewTriple(Resource subject, URI predicate,
			Value object, ManagableRdfDataUnit repository) throws RepositoryException {



        boolean isInRepository = false;

        RepositoryConnection connection = repository.getConnection();
        long size = connection.size(repository.getDataGraph());
        connection.hasStatement(subject, predicate, object, true, repository.getDataGraph());
        connection.add(subject, predicate, object, repository.getDataGraph());

		long expectedSize =  connection.size(repository.getDataGraph());

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

	private void extractBigDataFileToRepository() throws RepositoryException {

		String suffix = "bigdata.ttl";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getDataGraph());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

		long newSize = connection.size(rdfRepo.getDataGraph());

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
	protected void extractFromFileToRepository(ManagableRdfDataUnit repository) throws RepositoryException {
		String suffix = ".rdf";
		String baseURI = "";
		boolean useSuffix = true;
		HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;
        RepositoryConnection connection = repository.getConnection();
        long size = 0;
        long newSize = -1;
        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getDataGraph());            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


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

        try {
            load(fileName);
        } catch ( Exception ex) {
            fail(ex.getMessage());
        }
	}
}
