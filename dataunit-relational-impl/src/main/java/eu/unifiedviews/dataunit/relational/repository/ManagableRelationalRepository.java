package eu.unifiedviews.dataunit.relational.repository;

import java.sql.SQLException;

import eu.unifiedviews.dataunit.relational.DataUnitDatabaseConnectionProvider;

public interface ManagableRelationalRepository {

    public static final String BASE_DATABASE_NAME = "dataUnitDb";

    public static enum Type {
        IN_MEMORY
    }

    /**
     * Get SQL connection provider to the underlying database
     * 
     * @return Implementation of {@link DataUnitDatabaseConnectionProvider} which provides connections to the underlying database
     * @throws SQLException
     */
    DataUnitDatabaseConnectionProvider getDatabaseConnectionProvider();

    /**
     * Terminate all connections to the database and shutdown the database
     * 
     * @throws Exception
     */
    void release() throws Exception;

}
