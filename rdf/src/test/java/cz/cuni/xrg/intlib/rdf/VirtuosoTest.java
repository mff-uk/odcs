package cz.cuni.xrg.intlib.rdf;

import cz.cuni.xrg.intlib.commons.IntegrationTest;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.*;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class VirtuosoTest extends LocalRDFRepoTest {

	@BeforeClass
	public static void setUpLogger() {
		rdfRepo = VirtuosoRDFRepo
				.createVirtuosoRDFRepo();
		rdfRepo.cleanAllRepositoryData();
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
	@Override
	public void BIGDataTest() {
		super.BIGDataTest();
	}

	@Test
	@Override
	public void paralellPipelineRunning() {

		for (int i = 0; i < THREAD_SIZE; i++) {

			Thread task = new Thread(new Runnable() {
				@Override
				public void run() {

					VirtuosoRDFRepo virtuosoRepo = VirtuosoRDFRepo
							.createVirtuosoRDFRepo();
					virtuosoRepo.setDefaultGraph("http://myDefault");

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
