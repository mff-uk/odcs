package eu.unifiedviews.commons.relational.db;

/**
 * {@link DefaultDatabaseConfig} is configuration object for internal database wrapper
 * It provides communication parameters for database (db name, user name, password)
 * and optional connections pool parameters
 * By default, connections pool is used and max. connections pooled is set to 20
 * 
 * @author Tomas
 */
public class DefaultDatabaseConfig implements DatabaseWrapperConfigIF {

    private final static int DEFAULT_MAX_POOLED_CONNECTIONS = 20;

    private final static boolean DEFAULT_USE_CONNECTIONS_POOL = true;

    private String databaseURL;

    private String userName;

    private String password;

    private String jdbcDriverName;

    private int maxPooledConnections = DEFAULT_MAX_POOLED_CONNECTIONS;

    private boolean useConnectionPool = DEFAULT_USE_CONNECTIONS_POOL;

    public DefaultDatabaseConfig(String databaseName, String userName, String password, String jdbcDriverName) {
        this.databaseURL = databaseName;
        this.userName = userName;
        this.password = password;
        this.jdbcDriverName = jdbcDriverName;
    }

    @Override
    public String getDatabaseURL() {
        return this.databaseURL;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setNumberOfPooledConnections(int maxPooledConnections) {
        this.maxPooledConnections = maxPooledConnections;
    }

    @Override
    public int getMaxPooledConnections() {
        return this.maxPooledConnections;
    }

    public void setUseConnectionsPool(boolean useConnectionPool) {
        this.useConnectionPool = useConnectionPool;
    }

    @Override
    public boolean useConnectionsPool() {
        return this.useConnectionPool;
    }

    @Override
    public String getJDBCDriverName() {
        return this.jdbcDriverName;
    }

}
