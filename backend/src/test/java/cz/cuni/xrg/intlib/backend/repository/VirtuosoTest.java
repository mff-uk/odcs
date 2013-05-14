package cz.cuni.xrg.intlib.backend.repository;

import cz.cuni.xrg.intlib.backend.data.rdf.VirtuosoRDFRepo;
import static cz.cuni.xrg.intlib.backend.repository.LocalRDFRepoTest.logger;
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
    }

    @Before
    public void setUpVirtuoso() {
        rdfRepo = VirtuosoRDFRepo.createVirtuosoRDFRepo(HOSTNAME, PORT, USERNAME, PASSWORD, DEFAUTLGRAPH);

    }

    @AfterClass
    public static void cleaning() {
        rdfRepo.cleanAllRepositoryData();
    }
}
