/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.unifiedviews.commons.rdf.repository;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Škoda Petr
 */
public class RepositoryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryFactory.class);

    private String uri;
    
    private String user;
    
    private String password;

    /**
     *
     * @param uri URI of remote server.
     * @param user User name.
     * @param password User password.
     */
    public void setRemoteParameters(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
    }

    /**
     *
     * @param executionId
     * @param type
     * @param directory DataUnit's directory.
     * @return
     * @throws RDFException
     */
    public ManagableRepository create(Long executionId, ManagableRepository.Type type, String directory) throws RDFException {
        // Create repository.
        final ManagableRepository repository;
        switch (type) {
            case INMEMORY_RDF:
                repository = new InMemoryRDF(directory);
                break;
            case LOCAL_RDF:
                repository = new LocalRDF(directory);
                break;
            case REMOTE_RDF:
                repository = new RemoteRDF(uri, user, password, executionId);
                break;
            case VIRTUOSO:
                repository = new Virtuoso(uri, user, password);
                break;
            default:
                throw new RDFException("Unknown repository type: " + type.toString());
        }
        // Test connection.
        RepositoryConnection connection = null;
        try {
            connection = repository.getConnectionSource().getConnection();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not test initial connect to repository", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                }
            }
        }
        return repository;
    }

}
