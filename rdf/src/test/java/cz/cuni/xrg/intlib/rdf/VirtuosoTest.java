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

    private static final String HOSTNAME = "localhost";

    private static final String PORT = "1111";

    private static final String USERNAME = "dba";
    private static final String PASSWORD = "dba";
    private static final String DEFAUTLGRAPH = "http://default";
	
    @BeforeClass
    public static void setUpLogger() {
        rdfRepo = VirtuosoRDFRepo.createVirtuosoRDFRepo(HOSTNAME, PORT, USERNAME, PASSWORD, DEFAUTLGRAPH);
        rdfRepo.cleanAllRepositoryData();
    }

    @Override
    public void setUp() {
        try {
            outDir = Files.createTempDirectory("intlib-out");
            testFileDir = VirtuosoTest.class.getResource("/repository").getPath();
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
}
