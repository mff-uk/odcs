package eu.unifiedviews.commons.rdf.repository;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 *
 * @author Škoda Petr
 */
class LocalRDF implements ManagableRepository{

    private final Repository repository;

    private final File repositoryDirectory;

    /**
     * 
     * @param repositoryPath Unique path for each pipeline's repository.
     * @throws DataUnitException
     */
    public LocalRDF(String repositoryPath) throws DataUnitException {
        repositoryDirectory = new File(repositoryPath);
        if (!repositoryDirectory.isDirectory() && !repositoryDirectory.mkdirs()) {
            throw new DataUnitException("Could not create repository directory.");
        }
        repository = new SailRepository(new NativeStore(repositoryDirectory));
        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            throw new RuntimeException("Could not initialize repository.", ex);
        }        
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return new ConnectionSource(repository, false);
    }

    @Override
    public void release() throws DataUnitException {
        try {
            repository.shutDown();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Could not shutdown repository.", ex);
        }
    }

    @Override
    public void delete() throws DataUnitException {
        release();
        // Delete storage data directory.
        FileUtils.deleteQuietly(repositoryDirectory);
    }

}
