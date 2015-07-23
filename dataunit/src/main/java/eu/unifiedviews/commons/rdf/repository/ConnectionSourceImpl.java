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

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import eu.unifiedviews.commons.dataunit.core.ConnectionSource;

/**
 * Provides access to connections into working rdf repository.
 *
 * @author Škoda Petr
 */
class ConnectionSourceImpl implements ConnectionSource {

    /**
     * Underlying repository.
     */
    private final Repository repository;

    /**
     * If true then connection is considered to be unreliable and in case of failure the operation should be
     * tried again.
     */
    private final boolean retryOnFailure;

    /**
     * Used repository.
     *
     * @param repository
     * @param retryOnFailure
     */
    public ConnectionSourceImpl(Repository repository, boolean retryOnFailure) {
        this.repository = repository;
        this.retryOnFailure = retryOnFailure;
    }

    @Override
    public RepositoryConnection getConnection() throws RepositoryException {
        return repository.getConnection();
    }

    @Override
    public boolean isRetryOnFailure() {
        return retryOnFailure;
    }

    @Override
    public ValueFactory getValueFactory() {
        return repository.getValueFactory();
    }

}
