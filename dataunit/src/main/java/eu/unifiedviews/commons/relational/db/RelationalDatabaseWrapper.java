package eu.unifiedviews.commons.relational.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link RelationalDatabaseWrapper} is a wrapper around a relational database
 * It provides access to this database by creating a new in memory database based on {@link DatabaseWrapperConfigIF} configuration.
 * Database wrapper uses {@link PooledConnectionProvider}, which uses pool of database connections.
 * 
 * @author Tomas
 */
public class RelationalDatabaseWrapper implements DatabaseWrapperIF {

    private final ConnectionProviderIF connectionProvider;

    private final DatabaseWrapperConfigIF config;

    public RelationalDatabaseWrapper(DatabaseWrapperConfigIF config) throws Exception {
        this.connectionProvider = new PooledConnectionProvider(config);
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return this.connectionProvider.getConnection();
    }

    @Override
    public DatabaseWrapperConfigIF getConfiguration() {
        return this.config;
    }

    @Override
    public void shutdown() {
        this.connectionProvider.close();
    }

}
