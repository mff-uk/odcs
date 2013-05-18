package cz.cuni.xrg.intlib.backend.repository;

import cz.cuni.xrg.intlib.backend.data.rdf.VirtuosoRDF;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoTest extends LocalRDFRepoTest {

    public static final String HOSTNAME = "localhost";
    public static final String PORT = "1111";
    public static final String USERNAME = "dba";
    public static final String PASSWORD = "dba";
    public static final String DEFAUTLGRAPH = "http://default";

    @BeforeClass
    public static void setUpLogger() {
        logger = LoggerFactory.getLogger(VirtuosoTest.class);
        rdfRepo = VirtuosoRDF.createVirtuosoRDFRepo(HOSTNAME, PORT, USERNAME, PASSWORD, DEFAUTLGRAPH);
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

    @AfterClass
    public static void cleaning() {
        rdfRepo.cleanAllRepositoryData();

    }

    @Override
    public void cleanUp() {
        deleteDirectory(new File(outDir.toString()));
    }

    //@Test
    @Override
    public void BIGDataTest() {
        super.BIGDataTest();
    }


}
