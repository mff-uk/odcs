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

import eu.unifiedviews.commons.dataunit.core.ConnectionSource;

/**
 *
 * @author Škoda Petr
 */
public interface ManagableRepository {

    /**
     * Repository type.
     */
    public static enum Type {
        LOCAL_RDF,
        INMEMORY_RDF,
        REMOTE_RDF,
        VIRTUOSO
    }

    /**
     *
     * @return Connection source for this repository.
     */
    public ConnectionSource getConnectionSource();

    /**
     * Called on repository release. Can not be called after {@link #delete()}
     *
     * @throws eu.unifiedviews.commons.rdf.repository.RDFException
     */
    public void release() throws RDFException;

    /**
     * Delete repository. Can not be called after {@link #release()}
     * 
     * @throws eu.unifiedviews.commons.rdf.repository.RDFException
     */
    public void delete() throws RDFException;

}
