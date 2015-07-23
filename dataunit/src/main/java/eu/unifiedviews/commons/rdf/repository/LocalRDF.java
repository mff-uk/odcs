/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.commons.rdf.repository;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import eu.unifiedviews.commons.dataunit.core.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 *
 * @author Škoda Petr
 */
class LocalRDF implements ManagableRepository {

    private final Repository repository;

    private final File repositoryDirectory;

    /**
     * 
     * @param repositoryPath Unique path for each pipeline's repository.
     * @throws DataUnitException
     */
    public LocalRDF(String repositoryPath) throws RDFException {
        repositoryDirectory = new File(repositoryPath);
        if (!repositoryDirectory.isDirectory() && !repositoryDirectory.mkdirs()) {
            throw new RDFException("Could not create repository directory.");
        }
        repository = new SailRepository(new NativeStore(repositoryDirectory));
        try {
            repository.initialize();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not initialize repository.", ex);
        }        
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return new ConnectionSourceImpl(repository, false);
    }

    @Override
    public void release() throws RDFException {
        try {
            repository.shutDown();
        } catch (RepositoryException ex) {
            throw new RDFException("Could not shutdown repository.", ex);
        }
    }

    @Override
    public void delete() throws RDFException {
        release();
        // Delete storage data directory.
        FileUtils.deleteQuietly(repositoryDirectory);
    }

}
