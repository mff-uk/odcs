package cz.cuni.xrg.intlib.backend.repository;

import cz.cuni.xrg.intlib.backend.data.rdf.LocalRDF;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.data.rdf.WriteGraphType;
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
    protected Path outDir;
    /**
     * Path to directory with test input data
     */
    protected String testFileDir;
    /**
     * Local repository
     */
    protected static RDFDataRepository rdfRepo;
    protected static Logger logger = LoggerFactory.getLogger(LocalRDFRepoTest.class);

    @Before
    public void setUp() {
        try {
            pathRepo = Files.createTempDirectory("intlib-repo");
            outDir = Files.createTempDirectory("intlib-out");
            testFileDir = LocalRDFRepoTest.class.getResource("/repository").getPath();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        rdfRepo = LocalRDF.createLocalRepo(pathRepo.toString(),"localRepo");
    }

    @Test
    public void isRepositoryCreated() {
        assertNotNull(rdfRepo);
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
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            rdfRepo.loadRDFfromRepositoryToXMLFile(
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
            rdfRepo.loadRDFfromRepositoryToXMLFile(
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
            rdfRepo.loadRDFfromRepositoryToXMLFile(
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
            rdfRepo.loadRDFfromRepositoryToXMLFile(
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
            rdfRepo.loadRDFfromRepositoryToXMLFile(outDir.toString(), fileName, format);
            rdfRepo.loadRDFfromRepositoryToXMLFile(outDir.toString(), fileName, format);
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

        long size = rdfRepo.getTripleCountInRepository();

        rdfRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = rdfRepo.getTripleCountInRepository();

        assertTrue(newSize > size);
    }

    @Test
    public void extractN3FilesToRepository() {

        String suffix = ".n3";
        String baseURI = "";
        boolean useSuffix = true;

        long size = rdfRepo.getTripleCountInRepository();

        rdfRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = rdfRepo.getTripleCountInRepository();

        assertTrue(newSize > size);
    }

    @Test
    public void loadAllToXMLfile() {

        String fileName = "AllData_output.rdf";
        RDFFormat format = RDFFormat.RDFXML;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            rdfRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

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

            long sizeBefore = rdfRepo.getTripleCountInRepository();
            rdfRepo.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query);
            long sizeAfter = rdfRepo.getTripleCountInRepository();

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

            long sizeBefore = rdfRepo.getTripleCountInRepository();
            rdfRepo.extractfromSPARQLEndpoint(
                    endpointURL, defaultGraphUri, query, name, password, format);
            long sizeAfter = rdfRepo.getTripleCountInRepository();

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
            WriteGraphType graphType= WriteGraphType.MERGE;

            rdfRepo.loadtoSPARQLEndpoint(endpointURL, defaultGraphUri, name, password,graphType);


        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }

    }

    //*TEST TO DO !!!*/ @Test
    public void transformUsingSPARQLUpdate() {

        String namespace = "http://sport/hockey/";
        String subjectName = "Jagr";
        String predicateName = "playes_in";
        String objectName = "Dalas_Stars";

        String updateQuery = "DELETE { ?who ?what 'Dalas_Stars' } "
                + "INSERT { ?who ?what 'Boston_Bruins' } "
                + "WHERE { ?who ?what 'Dalas_Stars' }";

        rdfRepo.addTripleToRepository(
                namespace, subjectName, predicateName, objectName);

        boolean beforeUpdate = rdfRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);
        assertTrue(beforeUpdate);

        rdfRepo.transformUsingSPARQL(updateQuery);

        boolean afterUpdate = rdfRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);
        assertFalse(afterUpdate);
    }

    private void TEDextractFile1ToRepository() {

        String suffix = "ted4.ttl";
        String baseURI = "";
        boolean useSuffix = true;

        long size = rdfRepo.getTripleCountInRepository();

        rdfRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = rdfRepo.getTripleCountInRepository();

        assertTrue(newSize > size);
    }

    private void TEDextractFile2ToRepository() {

        String suffix = "ted4b.ttl";
        String baseURI = "";
        boolean useSuffix = true;

        long size = rdfRepo.getTripleCountInRepository();

        rdfRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = rdfRepo.getTripleCountInRepository();

        boolean triplesAdded = newSize > size;

        assertTrue(triplesAdded);
    }

    private void TEDTransformSPARQL() {

        String updateQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "INSERT DATA"
                + "{ "
                + "<http://example/book1> dc:title \"A new book\" ."
                + "}";

        rdfRepo.transformUsingSPARQL(updateQuery);

    }

    private void TEDloadtoTTLFile() {

        String fileName = "output-ted-test.ttl";
        RDFFormat format = RDFFormat.TURTLE;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            rdfRepo.loadRDFfromRepositoryToXMLFile(
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

        long addedData = rdfRepo.getTripleCountInRepository();

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

        rdfRepo.transformUsingSPARQL(updateQuery);
    }

    @Test
    public void isRepositoryEmpty() {
        rdfRepo.cleanAllRepositoryData();
        assertEquals(0, rdfRepo.getTripleCountInRepository());
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

        long size = rdfRepo.getTripleCountInRepository();
        boolean isInRepository = rdfRepo.isTripleInRepository(
                namespace, subjectName, predicateName, objectName);

        rdfRepo.addTripleToRepository(
                namespace, subjectName, predicateName, objectName);
        long expectedSize = rdfRepo.getTripleCountInRepository();

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
    protected static void deleteDirectory(File directory) {
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

    private void extractBigDataFileToRepository() {

        String suffix = "bigdata.ttl";
        String baseURI = "";
        boolean useSuffix = true;

        long size = rdfRepo.getTripleCountInRepository();

        rdfRepo.extractRDFfromXMLFileToRepository(
                testFileDir, suffix, baseURI, useSuffix);

        long newSize = rdfRepo.getTripleCountInRepository();

        logger.debug("EXTRACTING from FILE - OK");
        logger.debug("EXTRACT TOTAL: " + String.valueOf(newSize - size) + " triples.");

    }

    private void BigTransformQuery1() {

        // Dotaz nahrazuje vsechny objekty jejich spravnymi URI

        String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
                + "PREFIX adms: <http://www.w3.org/ns/adms#> "
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "DELETE { "
                + "  ?s ?p ?s1 . } "
                + "INSERT { "
                + "  ?s ?p ?s2 . } "
                + "WHERE { "
                + "  ?s1 owl:sameAs ?s2 . "
                + "  ?s ?p ?s1 . }";

        rdfRepo.transformUsingSPARQL(updateQuery);
        logger.debug("Transform Query 1 - OK");
    }

    private void BigTransformQuery2() {

        // Dotaz nahrazuje vsechny subjekty jejich spravnymi URI

        String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
                + "PREFIX adms: <http://www.w3.org/ns/adms#> "
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "DELETE { "
                + "  ?s1 ?p ?o . } "
                + "INSERT { "
                + "  ?s2 ?p ?o . } "
                + "WHERE { "
                + "  ?s1 owl:sameAs ?s2 . "
                + "  ?s ?p ?o . }";

        rdfRepo.transformUsingSPARQL(updateQuery);
        logger.debug("Transform Query 2 - OK");
    }

    private void BigTransformQuery3() {

        //Maze same-as na spatne URI

        String updateQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX ndf: <http://linked.opendata.cz/ontology/ndfrt/> "
                + "PREFIX adms: <http://www.w3.org/ns/adms#> "
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "DELETE { "
                + "  ?s1 owl:sameAs ?s2 . } "
                + "WHERE { "
                + "  ?s1 owl:sameAs ?s2 . }";


        rdfRepo.transformUsingSPARQL(updateQuery);
        logger.debug("Transform Query 3 - OK");
    }

    private void loadBigDataToN3File() {

        String fileName = "BIG_Data.n3";
        RDFFormat format = RDFFormat.N3;
        boolean canBeOverWriten = true;
        boolean isNameUnique = false;

        try {
            rdfRepo.loadRDFfromRepositoryToXMLFile(
                    outDir.toString(), fileName, format, canBeOverWriten, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            fail(ex.getMessage());
        }

        logger.debug("LOADING from FILE - OK");
    }

    //@Test
    public void BIGDataTest() {

        extractBigDataFileToRepository();
        BigTransformQuery1();
        BigTransformQuery2();
        BigTransformQuery3();
        loadBigDataToN3File();
    }
}
