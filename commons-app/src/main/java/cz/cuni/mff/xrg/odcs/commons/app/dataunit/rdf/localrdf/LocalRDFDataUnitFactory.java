package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;

public class LocalRDFDataUnitFactory implements RDFDataUnitFactory {
    private String repositoryPath;

    private final Map<String, Repository> initializedRepositories = new HashMap<String, Repository>();
    private final Map<String, Object> locks = new HashMap<String, Object>();

    private synchronized Object getLock(String id) {
        if (locks.containsKey(id)) {
            return locks.get(id);
        }
        Object lock = new Object();
        locks.put(id, lock);
        return lock;
    }
    
    @Override
    public ManagableRdfDataUnit create(String pipelineId, String dataUnitName, String dataGraph) {
        Repository repository = null;
        synchronized (getLock(pipelineId)) {
            repository = initializedRepositories.get(pipelineId);
            if (repository == null) {
                File managerDir = new File(repositoryPath);
                if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
                    throw new RuntimeException("Could not create repository manager directory.");
                }
                File repositoryDirectory = new File(managerDir, pipelineId);
                if (!repositoryDirectory.isDirectory() && !repositoryDirectory.mkdirs()) {
                    throw new RuntimeException("Could not create repository directory.");
                }

                repository = new SailRepository(new NativeStore(repositoryDirectory));
                try {
                    repository.initialize();
                } catch (RepositoryException ex) {
                    throw new RuntimeException("Could not initialize repository.", ex);
                }
                initializedRepositories.put(pipelineId, repository);
            }
        }

        return new LocalRDFDataUnit(repository, dataUnitName, dataGraph);
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    @Override
    public void clean(String pipelineId) {
        Repository repository = null;
        synchronized (getLock(pipelineId)) {
            repository = initializedRepositories.get(pipelineId);
            if (repository != null) {
                File managerDir = new File(repositoryPath);
                if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
                    throw new RuntimeException("Could not create repository manager directory.");
                }

                try {
                    repository.shutDown();
                } catch (RepositoryException ex) {
                    throw new RuntimeException("Could not shutdown repository.");
                }
                File repositoryDirectory = new File(managerDir, pipelineId);
                initializedRepositories.remove(pipelineId);
                FileUtils.deleteQuietly(repositoryDirectory);
            }
        }
    }

    @Override
    public void release(String pipelineId) {
        Repository repository = null;
        synchronized (getLock(pipelineId)) {
            repository = initializedRepositories.get(pipelineId);
            if (repository != null) {
                File managerDir = new File(repositoryPath);
                if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
                    throw new RuntimeException("Could not create repository manager directory.");
                }
                try {
                    repository.shutDown();
                } catch (RepositoryException ex) {
                    throw new RuntimeException("Could not shutdown repository.");
                }
                initializedRepositories.remove(pipelineId);
            }
        }
    }
}
