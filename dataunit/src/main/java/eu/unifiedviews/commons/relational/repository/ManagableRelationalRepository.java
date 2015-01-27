package eu.unifiedviews.commons.relational.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface ManagableRelationalRepository {

    public static final String BASE_DATABASE_NAME = "dataUnitDb";

    public static enum Type {
        IN_MEMORY
    }

    /**
     * Get SQL connection to the underlying database
     * 
     * @return
     * @throws SQLException
     */
    Connection getDatabaseConnection() throws SQLException;

    /**
     * Terminate all connections to the database and shutdown the database
     * 
     * @throws Exception
     */
    void release() throws Exception;

}
