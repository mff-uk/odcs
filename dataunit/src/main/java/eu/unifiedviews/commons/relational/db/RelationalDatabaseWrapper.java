package eu.unifiedviews.commons.relational.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link RelationalDatabaseWrapper} is a wrapper around an embedded, in memory H2 database engine.
 * It provides access to this database by creating a new in memory database based on {@link DatabaseWrapperConfigIF} configuration.
 * {@link RelationalDatabaseWrapper} provides two types of connection providers.
 * By default, it uses {@link PooledConnectionProvider}, which uses pool of database connections.
 * If desired, {@link ClassicConnectionProvider} can be used
 * 
 * @author Tomas
 */
public class RelationalDatabaseWrapper implements DatabaseWrapperIF {

    private final ConnectionProviderIF connectionProvider;

    private final DatabaseWrapperConfigIF config;

    public RelationalDatabaseWrapper(DatabaseWrapperConfigIF config) throws Exception {
        if (config.useConnectionsPool()) {
            this.connectionProvider = new PooledConnectionProvider(config);
        } else {
            this.connectionProvider = new ClassicConnectionProvider(config);
        }
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return this.connectionProvider.getConnection();
    }

    @Override
    public DatabaseWrapperConfigIF getConfiguration() {
        return this.config;
    }

}
