package cz.cuni.xrg.intlib.rdf;

import cz.cuni.xrg.intlib.commons.IntegrationTest;
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.*;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.fail;

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

	@BeforeClass
	public static void setUpLogger() {

		rdfRepo = VirtuosoRDFRepo.createVirtuosoRDFRepo(hostName, port, user,
				password, defaultGraph, "");
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
		rdfRepo.release();
	}

	@Test
	public void addDataUsingTransformer() {
		String query = "insert data {<http://test>  <http://test>  <http://test> .}";
		try {
			rdfRepo.transformUsingSPARQL(query);
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
		RDFDataRepository goal = VirtuosoRDFRepo.createVirtuosoRDFRepo(hostName,
				port, user, password, defaultGraph, "");
		goal.setDataGraph("http://goal");

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
		boolean useStatisticHandler = true;

		long size = rdfRepo.getTripleCount();

		try {
			rdfRepo.extractfromFile(FileExtractType.PATH_TO_FILE,
					testFileDir, suffix, baseURI, useSuffix, useStatisticHandler);
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

			Thread task = new Thread(new Runnable() {
				@Override
				public void run() {

					VirtuosoRDFRepo virtuosoRepo = VirtuosoRDFRepo
							.createVirtuosoRDFRepo(hostName, port, user,
							password, defaultGraph, "");
					virtuosoRepo.setDataGraph("http://myDefault");

					addParalelTripleToRepository(virtuosoRepo);
					extractFromFileToRepository(virtuosoRepo);
					transformOverRepository(virtuosoRepo);
					loadToFile(virtuosoRepo);

					virtuosoRepo.release();


				}
			});

			task.start();
		}
	}
}
