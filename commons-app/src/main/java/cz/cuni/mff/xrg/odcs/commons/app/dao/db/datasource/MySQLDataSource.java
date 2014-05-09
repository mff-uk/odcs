package cz.cuni.mff.xrg.odcs.commons.app.dao.db.datasource;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * MySQL data source.
 * 
 * @author Jan Vojt
 */
public class MySQLDataSource extends BasicDataSource {

    /**
     * Class name to be used as JDBC driver.
     */
    public static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    /**
     * JDBC connection string.
     */
    private static final String JDBC_URL = "jdbc:mysql://%s:%s/%s"
            + "?autoReconnect=true&useUnicode=true&characterEncoding=%s";

    /**
     * <code>DataSource</code> constructed from configuration.
     * 
     * @param config
     *            application configuration
     */
    public MySQLDataSource(AppConfig config) {
        setUrl(buildUrl(config));
        setUsername(config.getString(ConfigProperty.DATABASE_USER));
        setPassword(config.getString(ConfigProperty.DATABASE_PASSWORD));
        setDriverClassName(DRIVER_CLASS_NAME);

        // Disable autocommit so we can use transactions
        // for business units of work. Otherwise each SQL
        // query would be autocommited on completion.
        setDefaultAutoCommit(false);
    }

    /**
     * Connection URL factory.
     * 
     * @param config
     * @return connection string
     */
    private static String buildUrl(AppConfig config) {
        return String.format(JDBC_URL,
                config.getString(ConfigProperty.DATABASE_HOSTNAME),
                config.getString(ConfigProperty.DATABASE_PORT),
                config.getString(ConfigProperty.DATABASE_NAME),
                config.getString(ConfigProperty.DATABASE_CHARSET)
                );
    }

}
