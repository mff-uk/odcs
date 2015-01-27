package eu.unifiedviews.commons.relational.repository;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.commons.relational.db.DatabaseWrapperConfigIF;
import eu.unifiedviews.commons.relational.db.DatabaseWrapperIF;
import eu.unifiedviews.commons.relational.db.DefaultDatabaseConfig;
import eu.unifiedviews.commons.relational.db.RelationalDatabaseWrapper;
import eu.unifiedviews.dataunit.DataUnitException;

public class InMemoryRelationalDatabase implements ManagableRelationalRepository {

    public static final String DEFAULT_BASE_DB_URL = "jdbc:h2:mem:";

    public static final String DEFAULT_JDBC_DRIVER = "org.h2.Driver";

    private static final String USER_NAME = "inMemoryUser";

    private static final String PASSWORD = "dummyPsw";

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryRelationalDatabase.class);

    private String baseDatabaseURL;

    private String jdbcDriverName;

    private long executionId;

    private String databaseURL;

    private DatabaseWrapperIF databaseWrapper;

    public InMemoryRelationalDatabase(String baseDatabaseURL, String jdbcDriverName, long executionId) throws DataUnitException {
        this.baseDatabaseURL = (baseDatabaseURL != null) ? baseDatabaseURL : DEFAULT_BASE_DB_URL;
        this.jdbcDriverName = (jdbcDriverName != null) ? jdbcDriverName : DEFAULT_JDBC_DRIVER;
        this.executionId = executionId;
        this.databaseURL = this.baseDatabaseURL + ManagableRelationalRepository.BASE_DATABASE_NAME
                + "_" + String.valueOf(this.executionId);

        this.databaseWrapper = createDatabaseWrapper();
    }

    private DatabaseWrapperIF createDatabaseWrapper() throws DataUnitException {
        DatabaseWrapperConfigIF config = new DefaultDatabaseConfig(this.databaseURL, USER_NAME, PASSWORD, this.jdbcDriverName);
        DatabaseWrapperIF wrapper = null;
        try {
            wrapper = new RelationalDatabaseWrapper(config);
        } catch (Exception e) {
            throw new DataUnitException("Failed to create database wrapper for internal relational database", e);
        }

        return wrapper;
    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return this.databaseWrapper.getConnection();
    }

    @Override
    public void release() throws Exception {
        this.databaseWrapper.shutdown();
    }

}
