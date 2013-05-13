package cz.cuni.xrg.intlib.backend.repository;

import cz.cuni.xrg.intlib.backend.data.rdf.VirtuosoRDFRepo;
import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoTest {

    public static final String HOSTNAME = "localhost";
    public static final String PORT = "1111";
    public static final String USERNAME = "dba";
    public static final String PASSWORD = "dba";
    public static final String DEFAUTLGRAPH="";
    
    private VirtuosoRDFRepo virtuosoRepo;
    private static final Logger logger = LoggerFactory.getLogger(VirtuosoTest.class);
    
    @Before
    public void setUp() {
        virtuosoRepo= VirtuosoRDFRepo.createVirtuosoRDFRepo(HOSTNAME,PORT,USERNAME,PASSWORD,DEFAUTLGRAPH);
    }
    
    @Test
    public void isRepositoryCreated() {
        assertNotNull(virtuosoRepo);
    }
    
    @Test
    public void addTripleToRepositoryTest1() {

        String namespace = "http://school/catedra/";
        String subjectName = "KSI";
        String predicateName = "isResposibleFor";
        String objectName = "Lecture";

        testNewTriple(namespace, subjectName, predicateName, objectName);

    }

    @Test
    public void addTripleToRepositoryTest2() {
        String namespace = "http://human/person/";
        String subjectName = "Jirka";
        String predicateName = "hasFriend";
        String objectName = "Pavel";

        testNewTriple(namespace, subjectName, predicateName, objectName);
    }

    @Test
    public void addTripleToRepositoryTest3() {
        String namespace = "http://namespace/intlib/";
        String subjectName = "subject";
        String predicateName = "object";
        String objectName = "predicate";

        testNewTriple(namespace, subjectName, predicateName, objectName);
    }
    
    private void testNewTriple(String namespace,
            String subjectName,
            String predicateName,
            String objectName) {

        long size = virtuosoRepo.getTripleCountInRepository();
        boolean isInRepository = virtuosoRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);

        virtuosoRepo.addTripleToRepository(
                namespace, subjectName, predicateName, objectName);
        long expectedSize = virtuosoRepo.getTripleCountInRepository();

        if (isInRepository) {
            assertEquals(expectedSize, size);
        } else {
            assertEquals(expectedSize, size + 1L);
        }
    }
}
