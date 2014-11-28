package eu.unifiedviews.commons.rdf.repository;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;

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
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public void release() throws DataUnitException;

    /**
     * Delete repository. Can not be called after {@link #release()}
     * 
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public void delete() throws DataUnitException;

}
