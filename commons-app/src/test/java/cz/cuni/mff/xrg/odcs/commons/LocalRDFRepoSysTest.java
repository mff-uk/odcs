package cz.cuni.mff.xrg.odcs.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf.LocalRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;

/**
 * @author Jiri Tomes
 */
public class LocalRDFRepoSysTest {

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
    protected static ManagableRdfDataUnit rdfRepo;

    /**
     * Count of thread used in tests.
     */
    protected static final int THREAD_SIZE = 10;

    /**
     * Logging info about behavior method.
     */
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    final private String encode = "UTF-8";

    /**
     * Basic setting before test execution.
     */
    @Before
    public void setUp() {
        try {
            pathRepo = Files.createTempDirectory("intlib-repo");
            outDir = Files.createTempDirectory("intlib-out");
            testFileDir = LocalRDFRepoSysTest.class.getResource("/repository")
                    .getPath();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        rdfRepo = new LocalRDFDataUnit(new SailRepository(new NativeStore(new File(pathRepo.toFile(), "testtt"))),
                "myTestName", "http://default");
    }

    /**
     * Test if repository is created.
     */
    @Test
    public void isRepositoryCreated() {
        assertNotNull(rdfRepo);
    }

    private void load(String fileName) throws FileNotFoundException, RepositoryException, DataUnitException, RDFHandlerException {
        FileOutputStream out = new FileOutputStream(getFilePath(fileName));
        OutputStreamWriter os = new OutputStreamWriter(out, Charset.forName(encode));
        RDFWriter rdfWriter = Rio.createWriter(Rio.getWriterFormatForFileName(fileName), os);
        RepositoryConnection connection = rdfRepo.getConnection();
        connection.export(rdfWriter, RDFHelper.getGraphsURIArray(rdfRepo));
        connection.close();

    }

    /**
     * Test adding triple to repository.
     */
    @Test
    public void addTripleToRepositoryTest1() throws RepositoryException, DataUnitException {

        String namespace = "http://school/catedra/";
        String subjectName = "KSI";
        String predicateName = "isResposibleFor";
        String objectName = "Lecture";

        RepositoryConnection connection = null;
        try {
            connection = rdfRepo.getConnection();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
        }
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI(namespace + subjectName);
        URI predicate = factory.createURI(namespace + predicateName);
        Value object = factory.createLiteral(objectName);

        testNewTriple(subject, predicate, object, rdfRepo);
    }

    /**
     * Test adding triple to repository.
     */
    @Test
    public void addTripleToRepositoryTest2() throws RepositoryException, DataUnitException {
        String namespace = "http://human/person/";
        String subjectName = "Jirka";
        String predicateName = "hasFriend";
        String objectName = "Pavel";

        RepositoryConnection connection = rdfRepo.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI(namespace + subjectName);
        URI predicate = factory.createURI(namespace + predicateName);
        Value object = factory.createLiteral(objectName);

        testNewTriple(subject, predicate, object, rdfRepo);
        connection.close();
    }

    /**
     * Test adding triple to repository.
     */
    @Test
    public void addTripleToRepositoryTest3() throws RepositoryException, DataUnitException {
        String namespace = "http://namespace/intlib/";
        String subjectName = "subject";
        String predicateName = "object";
        String objectName = "predicate";

        RepositoryConnection connection = rdfRepo.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI(namespace + subjectName);
        URI predicate = factory.createURI(namespace + predicateName);
        Value object = factory.createLiteral(objectName);

        testNewTriple(subject, predicate, object, rdfRepo);
        connection.close();
    }

    private String getFilePath(String fileName) {
        File file = new File(outDir.toString(), fileName);
        return file.getAbsolutePath();
    }

    /**
     * Test loading data to RDF/XML file.
     */
    @Test
    public void loadRDFtoXMLFile() {

        String fileName = "RDF_output.ttl";

        try {
            load(fileName);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

    }

    /**
     * Test extracting data using Statistical handler.
     */
    @Test
    public void extractUsingStatisticHandler() throws RepositoryException, DataUnitException {
        String suffix = ".rdf";
        String baseURI = "";
        boolean useSuffix = true;
        HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getBaseDataGraphURI());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getBaseDataGraphURI());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        long newSize = connection.size(rdfRepo.getBaseDataGraphURI());

        connection.close();
        assertTrue(newSize > size);
    }

    /**
     * Test extracting data using not existed file.
     */
    @Test
    public void extractNotExistedFile() throws RepositoryException, DataUnitException {
        File dirFile = new File("NotExistedFile");

        String suffix = ".rdf";
        String baseURI = "";
        boolean useSuffix = true;
        HandlerExtractType handlerType = HandlerExtractType.ERROR_HANDLER_CONTINUE_WHEN_MISTAKE;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getBaseDataGraphURI());
        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getBaseDataGraphURI());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
        connection.close();

    }

    private void TEDextractFile1ToRepository() throws RepositoryException, DataUnitException {

        String suffix = "ted4.ttl";
        String baseURI = "";
        boolean useSuffix = true;
        HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getBaseDataGraphURI());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getBaseDataGraphURI());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        long newSize = connection.size(rdfRepo.getBaseDataGraphURI());
        connection.close();
        assertTrue(newSize > size);
    }

    private void TEDextractFile2ToRepository() throws RepositoryException, DataUnitException {

        String suffix = "ted4b.ttl";
        String baseURI = "";
        boolean useSuffix = true;
        HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;

        RepositoryConnection connection = rdfRepo.getConnection();
        long size = connection.size(rdfRepo.getBaseDataGraphURI());

        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getBaseDataGraphURI());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        long newSize = connection.size(rdfRepo.getBaseDataGraphURI());

        boolean triplesAdded = newSize > size;
        connection.close();
        assertTrue(triplesAdded);
    }

    /**
     * Test running pipeline 'TED' - 2 extraction, 1 transform, 1 load to TTL
     * file.
     */
    @Test
    public void TEDPipelineTest() throws RepositoryException, DataUnitException {
        TEDextractFile1ToRepository();
        TEDextractFile2ToRepository();

        RepositoryConnection connection = rdfRepo.getConnection();
        long addedData = connection.size(rdfRepo.getBaseDataGraphURI());
        connection.close();
        assertTrue(addedData > 0);
    }

    /**
     * Test if repository is empty.
     */
    @Test
    public void isRepositoryEmpty() throws RepositoryException, DataUnitException {
        RepositoryConnection connection = rdfRepo.getConnection();
        connection.clear(rdfRepo.getBaseDataGraphURI());
        assertEquals(0, connection.size(rdfRepo.getBaseDataGraphURI()));
        connection.close();
    }

    /**
     * Delete used repository files for testing.
     */
    @After
    public void cleanUp() {
        rdfRepo.clear();
        rdfRepo.release();
        deleteDirectory(pathRepo.toFile());
        deleteDirectory(new File(outDir.toString()));

    }

    private void testNewTriple(Resource subject, URI predicate,
            Value object, ManagableRdfDataUnit repository) throws RepositoryException, DataUnitException {

        boolean isInRepository = false;

        RepositoryConnection connection = repository.getConnection();
        long size = connection.size(repository.getBaseDataGraphURI());
        connection.hasStatement(subject, predicate, object, true, repository.getBaseDataGraphURI());
        connection.add(subject, predicate, object, repository.getBaseDataGraphURI());

        long expectedSize = connection.size(repository.getBaseDataGraphURI());

        if (isInRepository) {
            assertEquals(expectedSize, size);
        } else {
            assertEquals(expectedSize, size + 1L);
        }
        connection.close();
    }

    /**
     * Recursively deletes a directory, follows symbolic links
     *
     * @param directory
     *            directory you can delete.
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

    /**
     * Test extracting RDF data from given repository instance.
     *
     * @param repository
     *            repository used for extracting
     */
    protected void extractFromFileToRepository(ManagableRdfDataUnit repository) throws RepositoryException, DataUnitException {
        String suffix = ".rdf";
        String baseURI = "";
        boolean useSuffix = true;
        HandlerExtractType handlerType = HandlerExtractType.STANDARD_HANDLER;
        RepositoryConnection connection = repository.getConnection();
        long size = 0;
        long newSize = -1;
        File dir = new File(testFileDir);
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        for (File file : files) {
            try {
                RDFFormat fileFormat = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);
                connection.add(file, baseURI, fileFormat, rdfRepo.getBaseDataGraphURI());
            } catch (RDFParseException e) {
                //in this case - just skip this file
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        connection.close();
        assertTrue(newSize > size);
    }

    /**
     * Test loading to file based on give repository instance.
     *
     * @param repository
     *            repository used for loading
     */
    protected void loadToFile(ManagableRdfDataUnit repository) {
        String fileName = "TTL_output.ttl";

        try {
            load(fileName);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
