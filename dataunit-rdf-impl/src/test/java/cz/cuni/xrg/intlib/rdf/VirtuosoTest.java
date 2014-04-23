package cz.cuni.xrg.intlib.rdf;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFDataUnit;

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

	/**
	 * Basic setting before initializing test class.
	 */
    @BeforeClass
    public static void setUpLogger() throws RepositoryException {

        rdfRepo = RDFDataUnitFactory.createVirtuosoRDFRepo(
                hostName, port, user, password, "", defaultGraph);
        RepositoryConnection connection = rdfRepo.getConnection();
        connection.clear(rdfRepo.getDataGraph());
    }

	/**
	 * Basic setting before test execution.
	 */
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

	/**
	 * Delete used repository files for testing.
	 */
	@Override
	public void cleanUp() {
		deleteDirectory(new File(outDir.toString()));
	}

	/**
	 * Cleaning after ending test class.
	 */
	@AfterClass
	public static void cleaning() {
		rdfRepo.clear();
		rdfRepo.release();
	}

	/**
	 * Test adding data using transformer.
	 */
	@Test
	public void addDataUsingTransformer() {
		String query = "insert data {<http://test>  <http://test>  <http://test> .}";
		try {
			rdfRepo.executeSPARQLUpdateQuery(query);
		} catch (RDFException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Run 'BIG' pipeline - 3 transformer, 1 loader to N3 file.
	 */
	@Test
	@Override
	public void BIGDataTest() {
		super.BIGDataTest();
	}

	/**
	 * Extract file with size 2GB.
	 */
	@Test
	public void BIGTwoGigaFileExtraction() {
		//extractTwoGigaFile();
	}

	/**
	 * Create copy of repository.
	 */
	@Test
	public void repositoryCopy() {
		ManagableRdfDataUnit goal = RDFDataUnitFactory.createVirtuosoRDFRepo(
				hostName, port, user, password, "", "http://goal");
		try {
			goal.merge(rdfRepo);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Testing paralell pipeline running.
	 */
	@Test
	@Override
	public void paralellPipelineRunning() {

		for (int i = 0; i < THREAD_SIZE; i++) {

			final String namedGraph = "http://myDefault" + i;
			Thread task = new Thread(new Runnable() {
				@Override
				public void run() {

					VirtuosoRDFDataUnit virtuosoRepo = RDFDataUnitFactory
							.createVirtuosoRDFRepo(
							hostName,
							port,
							user,
							password,
							"",
							namedGraph);

					synchronized (virtuosoRepo) {
                        try {
                            addParalelTripleToRepository(virtuosoRepo);
                            extractFromFileToRepository(virtuosoRepo);
                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                        transformOverRepository(virtuosoRepo);
						loadToFile(virtuosoRepo);
					}
					virtuosoRepo.clear();
					virtuosoRepo.release();


				}
			});

			task.start();
		}
	}
}
