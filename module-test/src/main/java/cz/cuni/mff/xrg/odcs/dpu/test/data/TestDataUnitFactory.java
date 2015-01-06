package cz.cuni.mff.xrg.odcs.dpu.test.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;
import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.impl.FilesDataUnitFactory;
import eu.unifiedviews.dataunit.files.impl.ManageableWritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.impl.ManageableWritableRDFDataUnit;
import eu.unifiedviews.dataunit.rdf.impl.RDFDataUnitFactory;

/**
 * Create {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}s that can be used
 * in {@link cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment}.
 *
 * @author Petyr
 */
public class TestDataUnitFactory {

    /**
     * Counter for dataUnits id's and directories.
     */
    private int dataUnitIdCounter = 0;

    private final Object counterLock = new Object();

    /**
     * Working directory.
     */
    private final File workingDirectory;

    private final Map<String, Repository> initializedRepositories = new HashMap<>();

    private final RDFDataUnitFactory rdfFactory = new RDFDataUnitFactory();

    private final FilesDataUnitFactory filesFactory = new FilesDataUnitFactory();

    /**
     * Create a {@link TestDataUnitFactory} that use given directory as working
     * directory.
     *
     * @param workingDirectory
     *            Directory where to create working subdirectories
     *            for {@link cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit} that use local storage as RDF repository.
     */
    public TestDataUnitFactory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Create RDF data unit.
     *
     * @param name
     *            Name of the DataUnit.
     * @return New {@link ManagableRdfDataUnit}.
     * @throws RepositoryException
     * @throws java.io.IOException
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public ManageableWritableRDFDataUnit createRDFDataUnit(String name) throws RepositoryException, IOException, DataUnitException {
        synchronized (counterLock) {
            final String id = "dpu-test_" + Integer.toString(dataUnitIdCounter++) + "_" + name;
            final String namedGraph = GraphUrl.translateDataUnitId(id);
            String pipelineId = "test_env_" + String.valueOf(this.hashCode());

            File dataUnitWorkingDirectory = Files.createTempDirectory(FileSystems.getDefault().getPath(workingDirectory.getCanonicalPath()), pipelineId).toFile();

            Repository repository = initializedRepositories.get(pipelineId);
            if (repository == null) {
                repository = new SailRepository(new NativeStore(new File(workingDirectory, pipelineId)));
                repository.initialize();
                initializedRepositories.put(pipelineId, repository);
            }
            final ConnectionSource connectionSource = new ConnectionSource(repository, false);

            //public RDFDataUnitImpl(String dataUnitName, String writeContextString, ConnectionSource connectionSource)

            return rdfFactory.create(name, namedGraph, connectionSource, dataUnitWorkingDirectory);
        }
    }

    public ManageableWritableFilesDataUnit createFilesDataUnit(String name) throws RepositoryException, IOException, DataUnitException {
        synchronized (counterLock) {
            final String id = "dpu-test_" + Integer.toString(dataUnitIdCounter++) + "_" + name;
            final String namedGraph = GraphUrl.translateDataUnitId(id);
            String pipelineId = "test_env_" + String.valueOf(this.hashCode());

            File dataUnitWorkingDirectory = Files.createTempDirectory(FileSystems.getDefault().getPath(workingDirectory.getCanonicalPath()), pipelineId).toFile();
    
            Repository repository = initializedRepositories.get(pipelineId);
            if (repository == null) {
                repository = new SailRepository(new NativeStore(new File(workingDirectory, pipelineId)));
                repository.initialize();
                initializedRepositories.put(pipelineId, repository);
            }
            final ConnectionSource connectionSource = new ConnectionSource(repository, false);

            return filesFactory.create(name, namedGraph, connectionSource, dataUnitWorkingDirectory);
        }
    }
}
