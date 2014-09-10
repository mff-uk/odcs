package cz.cuni.mff.xrg.odcs.dpu.test.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf.LocalRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;

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

    private Object counterLock = new Object();

    /**
     * Working directory.
     */
    private final File workingDirectory;

    private final Map<String, Repository> initializedRepositories = new HashMap<String, Repository>();

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
     * @param useVirtuoso
     *            False to use local repository, True to use Virtuoso.
     * @return New {@link ManagableRdfDataUnit}.
     * @throws RepositoryException
     */
    public ManagableRdfDataUnit createRDFDataUnit(String name) throws RepositoryException {
        synchronized (counterLock) {
            final String id = "dpu-test_" + Integer.toString(dataUnitIdCounter++) + "_" + name;
            final String namedGraph = GraphUrl.translateDataUnitId(id);
            String pipelineId = "test_env_" + String.valueOf(this.hashCode());
            Repository repository = initializedRepositories.get(pipelineId);
            if (repository == null) {
                repository = new SailRepository(new NativeStore(new File(workingDirectory, pipelineId)));
                repository.initialize();
                initializedRepositories.put(pipelineId, repository);
            }

            return new LocalRDFDataUnit(
                    repository,
                    name,
                    namedGraph);
        }
    }
}
