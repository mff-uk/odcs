package cz.cuni.mff.xrg.odcs.rdf;

import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public interface RDFData {
    /**
     * Returns shared connection to repository.
     * 
     * @return Shared connection to repository.
     * @throws RepositoryException
     *             If something went wrong during the creation
     *             of the Connection.
     */
    public RepositoryConnection getConnection() throws RepositoryException;

    /**
     * Returns URI representation of graph where RDF data are stored.
     * 
     * @return URI representation of graph where RDF data are stored.
     */
    public Set<URI> getContexts();
}
