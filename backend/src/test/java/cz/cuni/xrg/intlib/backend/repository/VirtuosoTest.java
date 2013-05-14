package cz.cuni.xrg.intlib.backend.repository;

import cz.cuni.xrg.intlib.backend.data.rdf.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import org.junit.*;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
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
    public static void setUpVirtuoso() {
        rdfRepo = VirtuosoRDFRepo.createVirtuosoRDFRepo(HOSTNAME, PORT, USERNAME, PASSWORD, DEFAUTLGRAPH);
        logger = LoggerFactory.getLogger(VirtuosoTest.class);
    }
    
    @AfterClass
    public static void cleaning() {
        rdfRepo.cleanAllRepositoryData();
    }

    /*@Test
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

    @Test
    public void isRepositoryEmpty() {
        virtuosoRepo.cleanAllRepositoryData();
        assertEquals(0, virtuosoRepo.getTripleCountInRepository());
    }
    
    @Test
    public void loadRDFtoXMLFile() {

        String fileName = "RDF_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            virtuosoRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoN3File() {

        String fileName = "N3_output.n3";
        RDFFormat format = RDFFormat.N3;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoTRIGFile() {

        String fileName = "TRIG_output.trig";
        RDFFormat format = RDFFormat.TRIG;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoTURTLEFile() {

        String fileName = "TURTLE_output.ttl";
        RDFFormat format = RDFFormat.TURTLE;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadOverWriteFail() {

        String fileName = "CanNotOverWrite_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(outDir.toString(), fileName, format);
            localRepo.loadRDFfromRepositoryToXMLFile(outDir.toString(), fileName, format);
            fail();

        } catch (CannotOverwriteFileException ex) {
            // test passed
        }
    }

    @Test
    public void extractRDFFilesToRepository() {

        String suffix = ".rdf";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        assertTrue(newSize > size);
    }

    @Test
    public void extractN3FilesToRepository() {

        String suffix = ".n3";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        assertTrue(newSize > size);
    }

    @Test
    public void loadAllToXMLfile() {

        String fileName = "AllData_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }
    
    @Test
    public void transformUsingSPARQLUpdate() {

        String namespace = "http://sport/hockey/";
        String subjectName = "Jagr";
        String predicateName = "playes_in";
        String objectName = "Dalas_Stars";

        String updateQuery = "DELETE { ?who ?what 'Dalas_Stars' }"
                + "INSERT { ?who ?what 'Boston_Bruins' }"
                + "WHERE { ?who ?what 'Dalas_Stars' }";

        localRepo.addTripleToRepository(
                namespace, subjectName, predicateName, objectName);

        boolean beforeUpdate = localRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);
        assertTrue(beforeUpdate);

        localRepo.transformUsingSPARQL(updateQuery);

        boolean afterUpdate = localRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);
        assertFalse(afterUpdate);
    }

    private void TEDextractFile1ToRepository() {

        String suffix = "ted4.ttl";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        assertTrue(newSize > size);
    }

    private void TEDextractFile2ToRepository() {

        String suffix = "ted4b.ttl";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        boolean triplesAdded = newSize > size;

        assertTrue(triplesAdded);
    }

    private void TEDTransformSPARQL() {

        String updateQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "INSERT DATA"
                + "{ "
                + "<http://example/book1> dc:title \"A new book\" ."
                + "}";

        localRepo.transformUsingSPARQL(updateQuery);

    }

    private void TEDloadtoTTLFile() {

        String fileName = "output-ted-test.ttl";
        RDFFormat format = RDFFormat.TURTLE;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void TEDPipelineTest() {
        TEDextractFile1ToRepository();
        TEDextractFile2ToRepository();
        TEDTransformSPARQL();
        TEDloadtoTTLFile();

        long addedData = localRepo.getTripleCountInRepository();

        assertTrue(addedData > 0);
    }

    @Test
    public void SecondUpdateQueryTest() {

        String updateQuery = "prefix s: <http://schema.org/>"
                + "DELETE {?s s:streetAddress ?o}"
                + "INSERT {?s s:streetAddress ?x}"
                + "WHERE {"
                + "{SELECT ?s ?o ?x"
                + "WHERE {{?s s:streetAddress ?o}}} FILTER (BOUND(?x))}";

        localRepo.transformUsingSPARQL(updateQuery);
    }

    

    private void testNewTriple(String namespace,
            String subjectName,
            String predicateName,
            String objectName) {

        Resource graph = virtuosoRepo.getGraph();

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
    }*/
}
