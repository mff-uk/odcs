package eu.unifiedviews.commons.relational.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * {@link ClassicConnectionProvider} provides connections to the underlying database
 * Each time a connection is requested, new database connection is created and returned
 * Caller is responsible for proper handling and closing of the connection
 * 
 * @author Tomas
 */
public class ClassicConnectionProvider implements ConnectionProviderIF {

    private final String databaseURL;

    private final Properties connectionProperties;

    public ClassicConnectionProvider(DatabaseWrapperConfigIF config) throws Exception {
        initializeDriver(config);
        this.connectionProperties = createConnectionProperties(config);
        this.databaseURL = config.getDatabaseURL();
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databaseURL, this.connectionProperties);
        conn.setAutoCommit(false);

        return conn;
    }

    private static void initializeDriver(DatabaseWrapperConfigIF config) throws Exception {
        try {
            Class.forName(config.getJDBCDriverName());
        } catch (ClassNotFoundException e) {
            throw new Exception("Failed to load database driver", e);
        }
    }

    private static Properties createConnectionProperties(DatabaseWrapperConfigIF config) {
        Properties props = new Properties();
        props.put("user", config.getUserName());
        props.put("password", config.getPassword());

        return props;
    }

    @Override
    public void close() {
        // nothing to do
    }

}
