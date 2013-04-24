package cz.cuni.xrg.intlib.repository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.*;
import org.junit.*;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRepoTest {

    private static final String pathRepo = "C:\\intlib\\myRepository";
    private static final String testFileDirectory=new File("Input_Test_Files").getAbsolutePath();
    private static LocalRepo localRepo;

    
    @BeforeClass
    public static void RepositorySucecessfulCreate() {
        localRepo = LocalRepo.createLocalRepo(pathRepo);
    }
    
    @Test
    public void isRepositoryCreated()
    {
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
        
        String path =testFileDirectory;
         
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
        String path = testFileDirectory;
        
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
    public void extractDataFromSPARQLEndpointTest() {
        try {
            URL endpointURL = new URL("http://dbpedia.org/sparql");
            String defaultGraphUri = "http://dbpedia.org";
            String query = "select * where {?s ?o ?p} LIMIT 50";

            long sizeBefore = localRepo.getTripleCountInRepository();
            localRepo.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query);
            long sizeAfter = localRepo.getTripleCountInRepository();

            boolean addValues = sizeBefore < sizeAfter;
            assertTrue(addValues);

        } catch (MalformedURLException ex) {
            System.err.println("Bad URL for SPARQL endpoint");
            System.err.println(ex.getMessage());

        }
    }

    @Test
    public void extractDataFromSPARQLEndpointNamePasswordTest() {
        try {
            URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
            String defaultGraphUri = "";
            String query = "select * where {?s ?o ?p} LIMIT 10";
            String name="SPARQL";
            String password="nejlepsipaper";
            
            RDFFormat format = RDFFormat.N3;

            long sizeBefore = localRepo.getTripleCountInRepository();
            localRepo.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query, name, password, format);
            long sizeAfter = localRepo.getTripleCountInRepository();

            boolean addValues = sizeBefore < sizeAfter;
            assertTrue(addValues);

        } catch (MalformedURLException ex) {
            System.err.println("Bad URL for SPARQL endpoint");
            System.err.println(ex.getMessage());

        }
    }

    @Test
    public void loadDataToSPARQLEndpointTest() {
        try {
            URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql");
            String defaultGraphUri = "http://ld.opendata.cz/resource/myGraph/001";

            localRepo.loadtoSPARQLEndpoint(endpointURL, defaultGraphUri);


        } catch (MalformedURLException ex) {
            System.err.println("Bad URL for SPARQL endpoint");
        }

    }

    @Test
    public void transformUsingSPARQLUpdate() {
        
        String namespace = "http://sport/hockey/";
        String subjectName = "Jagr";
        String predicateName = "playes_in";
        String objectName = "Dalas_Stars";
        
        String updateQuery = "DELETE { ?who ?what 'Dalas_Stars' }" 
                +"INSERT { ?who ?what 'Boston_Bruins' }" 
                +"WHERE { ?who ?what 'Dalas_Stars' }";
        
        cleanRepository();
        
        localRepo.addTripleToRepository(namespace, subjectName, predicateName, objectName);
        
        boolean beforeUpdate=localRepo.isTripleInRepository(namespace, subjectName, predicateName, objectName);
        assertTrue(beforeUpdate);
        
        localRepo.transformUsingSPARQL(updateQuery);
        
        boolean afterUpdate=localRepo.isTripleInRepository(namespace, subjectName, predicateName,objectName);
        assertFalse(afterUpdate);
        
    }
    
    
    private void TEDextractFile1ToRepository() {
        String path = "http://ld.opendata.cz/tedDumps/ted4.ttl";
        String suffix = "";
        String baseURI = "";
        boolean useSuffix = false;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useSuffix);

        long newSize = localRepo.getTripleCountInRepository();

        boolean triplesAdded = newSize > size;

        assertTrue(triplesAdded);
    }
    
    
    private void TEDextractFile2ToRepository() {
        String path = "http://ld.opendata.cz/tedDumps/ted4b.ttl";
        String suffix = "";
        String baseURI = "";
        boolean useSuffix = true;

        long size = localRepo.getTripleCountInRepository();

        localRepo.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useSuffix);

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
        String path = "C:\\intlib\\Output_Test_Files\\";
        String fileName = "output-ted-test.ttl";
        RDFFormat format = RDFFormat.TURTLE;
        boolean canFileOverwritte = true;

        try {
            localRepo.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);

        } catch (FileCannotOverwriteException ex) {
            fail(ex.getMessage());
        }
    }
    
    @Test
    public void TEDPipelineTest()
    {
        cleanRepository();
        
        TEDextractFile1ToRepository();
        TEDextractFile2ToRepository();
        TEDTransformSPARQL();
        TEDloadtoTTLFile();
        
        boolean addedData=localRepo.getTripleCountInRepository()>0;
        
        assertTrue(addedData);
    }
    
    @Test
    public void isRepositoryEmpty()
    {
        localRepo.cleanAllRepositoryData();
        assertEquals(0, localRepo.getTripleCountInRepository());
    }

    @AfterClass
    public static void cleanRepository() {
        localRepo.cleanAllRepositoryData();
        
    }
}
