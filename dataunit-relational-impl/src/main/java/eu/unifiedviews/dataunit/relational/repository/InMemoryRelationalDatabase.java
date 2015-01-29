package eu.unifiedviews.dataunit.relational.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.DataUnitDatabaseConnectionProvider;
import eu.unifiedviews.dataunit.relational.db.DatabaseWrapperConfigIF;
import eu.unifiedviews.dataunit.relational.db.DatabaseWrapperIF;
import eu.unifiedviews.dataunit.relational.db.DefaultDatabaseConfig;
import eu.unifiedviews.dataunit.relational.db.RelationalDatabaseWrapper;

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

        LOG.debug("Creating dataunit in memory database with URL: {} and dummy user name: {}", this.databaseURL, USER_NAME);
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
    public void release() throws Exception {
        LOG.debug("Releasing data unit relational repository");
        this.databaseWrapper.shutdown();
    }

    @Override
    public DataUnitDatabaseConnectionProvider getDatabaseConnectionProvider() {
        return new DataUnitDatabaseConnectionProviderImpl(this.databaseWrapper);
    }

}
