package eu.unifiedviews.dataunit.relational.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link RelationalDatabaseWrapper} is a wrapper around a relational database
 * It provides access to this database by creating a new in memory database based on {@link DatabaseWrapperConfigIF} configuration.
 * Database wrapper uses {@link PooledDatabaseConnectionImpl}, which uses pool of database connections.
 */
public class RelationalDatabaseWrapper implements DatabaseWrapperIF {

    private final DatabaseConnection connectionProvider;

    private final DatabaseWrapperConfigIF config;

    private boolean bActive = true;

    /**
     * Creates new wrapper around the relational database defined by {@link DatabaseWrapperConfigIF}
     * 
     * @param config
     *            Connection parameters for the database
     * @throws Exception
     */
    public RelationalDatabaseWrapper(DatabaseWrapperConfigIF config) throws Exception {
        this.connectionProvider = new PooledDatabaseConnectionImpl(config);
        this.config = config;
    }

    @Override
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
        this.bActive = false;
    }

    @Override
    public boolean isActive() {
        return this.bActive;
    }

}
