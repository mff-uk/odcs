package cz.cuni.xrg.intlib.backend.repository;

import cz.cuni.xrg.intlib.backend.data.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.*;
import static org.junit.Assert.*;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRDFRepoTest {

    /**
     * Path to test repository
     */
    private Path pathRepo;
    /**
     * Path to directory with produced data
     */
    private Path outDir;
    /**
     * Path to directory with test input data
     */
    private String testFileDir;
    /**
     * Local repository
     */
    private LocalRDFRepo localRepo;
    private static final Logger logger = LoggerFactory.getLogger(LocalRDFRepoTest.class);

    @Before
    public void setUp() {
        try {
            pathRepo = Files.createTempDirectory("intlib-repo");
            outDir = Files.createTempDirectory("intlib-out");
            testFileDir = LocalRDFRepoTest.class.getResource("/repository").getPath();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        localRepo = LocalRDFRepo.createLocalRepo(pathRepo.toString());
    }

    @Test
    public void isRepositoryCreated() {
        assertNotNull(localRepo);
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

        String fileName = "RDF_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;
        boolean canBeOverWriten=true;
        boolean isNameUnique=false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten,isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoN3File() {

        String fileName = "N3_output.n3";
        RDFFormat format = RDFFormat.N3;
        boolean canBeOverWriten=true;
        boolean isNameUnique=false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten,isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoTRIGFile() {

        String fileName = "TRIG_output.trig";
        RDFFormat format = RDFFormat.TRIG;
        boolean canBeOverWriten=true;
        boolean isNameUnique=false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten,isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void loadRDFtoTURTLEFile() {

        String fileName = "TURTLE_output.ttl";
        RDFFormat format = RDFFormat.TURTLE;
        boolean canBeOverWriten=true;
        boolean isNameUnique=false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten,isNameUnique);

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
        boolean canBeOverWriten=true;
        boolean isNameUnique=false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten,isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * This is not unit test, as it depends on remote server -> commented out
     * for build, use only when debugging
     */
//    @Test
    public void extractDataFromSPARQLEndpointTest() {

        try {
            URL endpointURL = new URL("http://dbpedia.org/sparql");
            String defaultGraphUri = "http://dbpedia.org";
            String query = "select * where {?s ?o ?p} LIMIT 50";

            long sizeBefore = localRepo.getTripleCountInRepository();
            localRepo.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query);
            long sizeAfter = localRepo.getTripleCountInRepository();

            assertTrue(sizeBefore < sizeAfter);

        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    /**
     * This is not unit test, as it depends on remote server -> commented out
     * for build, use only when debugging
     */
//    @Test
    public void extractDataFromSPARQLEndpointNamePasswordTest() {
        try {
            URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
            String defaultGraphUri = "";
            String query = "select * where {?s ?o ?p} LIMIT 10";
            String name = "SPARQL";
            String password = "nejlepsipaper";

            RDFFormat format = RDFFormat.N3;

            long sizeBefore = localRepo.getTripleCountInRepository();
            localRepo.extractfromSPARQLEndpoint(
                    endpointURL, defaultGraphUri, query, name, password, format);
            long sizeAfter = localRepo.getTripleCountInRepository();

            assertTrue(sizeBefore < sizeAfter);

        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }
    }

    /**
     * This is not unit test, as it depends on remote server -> commented out
     * for build, use only when debugging
     */
//    @Test
    public void loadDataToSPARQLEndpointTest() {
        try {
            URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
            String defaultGraphUri = "http://ld.opendata.cz/resource/myGraph/001";
            String name = "SPARQL";
            String password = "nejlepsipaper";

            localRepo.loadtoSPARQLEndpoint(endpointURL, defaultGraphUri, name, password);


        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
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
        boolean canBeOverWriten=true;
        boolean isNameUnique=false;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten,isNameUnique);

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

    @Test
    public void isRepositoryEmpty() {
        localRepo.cleanAllRepositoryData();
        assertEquals(0, localRepo.getTripleCountInRepository());
    }

    @After
    public void cleanUp() {
        deleteDirectory(pathRepo.toFile());
        deleteDirectory(new File(outDir.toString()));
    }

    private void testNewTriple(String namespace,
            String subjectName,
            String predicateName,
            String objectName) {

        long size = localRepo.getTripleCountInRepository();
        boolean isInRepository = localRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);

        localRepo.addTripleToRepository(
                namespace, subjectName, predicateName, objectName);
        long expectedSize = localRepo.getTripleCountInRepository();

        if (isInRepository) {
            assertEquals(expectedSize, size);
        } else {
            assertEquals(expectedSize, size + 1L);
        }
    }

    /**
     * Recursively deletes a directory, follows symbolic links
     *
     * @param directory
     */
    private static void deleteDirectory(File directory) {
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
}
