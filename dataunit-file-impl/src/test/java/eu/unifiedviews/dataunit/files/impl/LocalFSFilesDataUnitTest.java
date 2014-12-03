package eu.unifiedviews.dataunit.files.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.unifiedviews.commons.rdf.repository.ManagableRepository;
import eu.unifiedviews.commons.rdf.repository.RDFException;
import eu.unifiedviews.commons.rdf.repository.RepositoryFactory;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

/**
 *
 * @author Škoda Petr
 */
public class LocalFSFilesDataUnitTest {

    private Path rootDir;

    private Path rootDirFile;

    private ManagableRepository repository;

    @Before
    public void prepare() throws IOException, DataUnitException, RDFException {
        RepositoryFactory factory = new RepositoryFactory();
        rootDir = Files.createTempDirectory(FileUtils.getTempDirectory().toPath(), "uv-filesDataUnit-");
        rootDirFile = (new File(rootDir.toFile(), "storage")).toPath();
        factory.setLocalParameters(rootDir.toAbsolutePath().toString());
        repository = factory.create("1", ManagableRepository.Type.LOCAL_RDF);
    }

    @After
    public void cleanUp() throws IOException, DataUnitException, RDFException {
        repository.delete();
        Files.deleteIfExists(rootDirFile);
        // There should be no data as we called clear.
        Assert.assertTrue("Failed to delete data directory", Files.deleteIfExists(rootDir));
    }

    @Test
    public void addFilesAndIterate() throws DataUnitException {
        final FilesDataUnitFactory factory  = new FilesDataUnitFactory();
        final ManageableWritableFilesDataUnit dataUnit = factory.create("test", "http://unifiedviews.eu/test/write", repository.getConnectionSource(), rootDirFile.toFile());
        // Add initial files.
        dataUnit.addNewFile("myFileA");
        dataUnit.addNewFile("myFileB");
        // Just check for size.
        int counter = 0;
        FilesDataUnit.Iteration iter = dataUnit.getIteration();
        while (iter.hasNext()) {
            FilesDataUnit.Entry entry = iter.next();
            ++counter;
        }
        Assert.assertEquals(2, counter);
        // Call clear add new file and again check.
        dataUnit.clear();
        dataUnit.addNewFile("myFileC");
        counter = 0;
        iter = dataUnit.getIteration();
        while (iter.hasNext()) {
            FilesDataUnit.Entry entry = iter.next();
            ++counter;
        }
        Assert.assertEquals(1, counter);
        // Clear and release.
        dataUnit.clear();
        dataUnit.release();
    }


}
