package eu.unifiedviews.dataunit.relational.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database repository interface
 * Defines the basic methods required by database repository wrapper
 * 
 * @author Tomas
 */
public interface DatabaseWrapperIF {
    /**
     * Get connection to the underlying database
     * 
     * @return Connection to the underlying database
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Get configuration of the database wrapper
     * 
     * @return Database wrapper configuration
     */
    DatabaseWrapperConfigIF getConfiguration();

    /**
     * 
     */
    void shutdown();

}
