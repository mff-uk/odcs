package eu.unifiedviews.commons.rdf.repository;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;
import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 *
 * @author Škoda Petr
 */
class Virtuoso implements ManagableRepository {

    private final Repository repository;

    public Virtuoso(String url, String user, String password) throws RDFException {
        repository = new VirtuosoRepository(url, user, password);
        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not initialize repository", ex);
        }
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return new ConnectionSource(repository, false);
    }

    @Override
    public void release() throws RDFException {
        try {
            repository.shutDown();
        } catch (RepositoryException ex) {
            throw new RDFException("Can't shutDown repository.", ex);
        }
    }

    @Override
    public void delete() throws RDFException {
        // Do nothing here.
    }

}
