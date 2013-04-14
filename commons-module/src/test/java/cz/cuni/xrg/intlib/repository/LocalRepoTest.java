package cz.cuni.xrg.intlib.repository;

import static org.junit.Assert.*;
import org.junit.*;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRepoTest {

    private final String pathRepo = "C:\\intlib\\myRepository";
    private static LocalRepo localRepo;

    @Before
    @Test
    public void isRepositorySucecessfulCreatedTest() {
        localRepo = LocalRepo.createLocalRepo(pathRepo);
        assertNotNull(localRepo);
    }

    private void testNewTriple(String namespace, String subjectName, String predicateName, String objectName) {
        long size = localRepo.getTripleCountInRepository();
        boolean isInRepository = localRepo.isTripleInRepository(namespace, subjectName, predicateName, objectName);

        localRepo.addTripleToRepository(namespace, subjectName, predicateName, objectName);
        long expectedSize = localRepo.getTripleCountInRepository();

        if (isInRepository) {
            assertEquals(expectedSize, size);
        } else {
            assertEquals(expectedSize, size + 1L);
        }
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
    public void loadRDFtoXMLFile() {
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "RDF_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;
        boolean canFileOverwritte = true;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);

        } catch (FileCannotOverwriteException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoN3File() {
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "N3_output.n3";
        RDFFormat format = RDFFormat.N3;
        boolean canFileOverwritte = true;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);

        } catch (FileCannotOverwriteException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoTRIGFile() {
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "TRIG_output.trig";
        RDFFormat format = RDFFormat.TRIG;
        boolean canFileOverwritte = true;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);

        } catch (FileCannotOverwriteException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoTURTLEFile() {
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "TURTLE_output.ttl";
        RDFFormat format = RDFFormat.TURTLE;
        boolean canFileOverwritte = true;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);

        } catch (FileCannotOverwriteException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadOverWriteFail() {
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "CanNotOverWrite_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format);
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format);
            fail();

        } catch (FileCannotOverwriteException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void extractRDFFilesToRepository() {
        String path = "C:\\intlib\\Input_Test_Files\\";
        String suffix = ".rdf";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        boolean triplesAdded = newSize > size;

        assertTrue(triplesAdded);
    }

    @Test
    public void extractN3FilesToRepository() {
        String path = "C:\\intlib\\Input_Test_Files\\";
        String suffix = ".n3";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        boolean triplesAdded = newSize > size;

        assertTrue(triplesAdded);
    }

    @Test
    public void loadAllToXMLfile() {
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "AllData_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;
        boolean canFileOverwritte = true;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);

        } catch (FileCannotOverwriteException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void cleanRepository() {
        localRepo.cleanAllRepositoryData();
        assertEquals(0, localRepo.getTripleCountInRepository());
    }
}
