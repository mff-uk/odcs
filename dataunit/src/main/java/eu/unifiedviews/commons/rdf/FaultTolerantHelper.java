/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unifiedviews.commons.rdf;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 *
 * @author Škoda Petr
 */
public class FaultTolerantHelper {

    /**
     * Contains code to execute. This can be replaced by lambda functions in Java 8.
     * If {@link RepositoryException} is thrown then the given code is considered to fail for external
     * reason. If pipeline code fail for external reason then the code is re-executed.
     */
    public interface Code {

        /**
         * Code to execute.
         *
         * @param connection
         * @throws org.openrdf.repository.RepositoryException
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        void execute(RepositoryConnection connection) throws RepositoryException, DataUnitException;

    }

    private static final Logger LOG = LoggerFactory.getLogger(FaultTolerantHelper.class);

    private FaultTolerantHelper() {

    }

    /**
     * Execute given code with some level of fault tolerance.
     *
     * @param connectionSource
     * @param codeToExecute
     * @throws org.openrdf.repository.RepositoryException
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public static void execute(ConnectionSource connectionSource, Code codeToExecute) throws RepositoryException, DataUnitException {
        if (innerExecute(connectionSource, codeToExecute)) {
            // Success.
        } else {
            // TODO Petr Škoda: Use user parametrization for attemps count and wait time.
            for (int i = 2; i < 6; ++i) {
                // Wait for some time.
                try {
                    LOG.debug("Sleeping before {} attempt.", i);
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOG.info("Interrupted!");
                    // Re-interrupt.
                    Thread.currentThread().interrupt();
                }
                // Try again.
                if (innerExecute(connectionSource, codeToExecute)) {
                    return;
                }
            }
            // If we are here we fail to execute user code.
            throw new DataUnitException("Failed to execute user given code.");
        }
    }

    /**
     *
     * @param connectionSource
     * @param codeToExecute
     * @return False if operation fail.
     * @throws RepositoryException
     * @throws DataUnitException
     */
    private static boolean innerExecute(ConnectionSource connectionSource, Code codeToExecute) throws RepositoryException, DataUnitException {
        RepositoryConnection connection = null;
        try {
            connection = connectionSource.getConnection();
            connection.begin();
            // Execute user given code.
            codeToExecute.execute(connection);
            connection.commit();
            // Success.
            return true;
        } catch (RepositoryException ex) {
            if (connectionSource.isRetryOnFailure()) {
                LOG.error("Operation failed for RepositoryException, we may try again.", ex);
                return false;
            } else {
                // Re-throw, instant failure.
                throw ex;
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (RepositoryException ex) {
                LOG.warn("Error when closing connection.", ex);
            }
        }
    }

}
