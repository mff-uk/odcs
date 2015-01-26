package eu.unifiedviews.commons.relational.repository;

import java.sql.Connection;
import java.sql.SQLException;

import eu.unifiedviews.commons.relational.db.DatabaseWrapperIF;

public class InMemoryRelationalDatabase implements ManagableRelationalRepository {

    public static final String DEFAULT_BASE_DB_URL = "jdbc:h2:mem:";

    public static final String DEFAULT_JDBC_DRIVER = "org.h2.Driver";

    private static final String USER_NAME = "inMemoryUser";

    private static final String PASSWORD = "dummyPsw";

    private String baseDatabaseURL;

    private String jdbcDriverName;

    private long executionId;

    private String databaseURL;

    private DatabaseWrapperIF databaseWrapper;

    public InMemoryRelationalDatabase(String baseDatabaseURL, String jdbcDriverName, long executionId) {
        this.baseDatabaseURL = (baseDatabaseURL != null) ? baseDatabaseURL : DEFAULT_BASE_DB_URL;
        this.jdbcDriverName = (jdbcDriverName != null) ? jdbcDriverName : DEFAULT_JDBC_DRIVER;
        this.executionId = executionId;
        this.databaseURL = ManagableRelationalRepository.BASE_DATABASE_NAME + "_" + String.valueOf(executionId);
        // TODO: create database wrapper from config
    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void release() throws Exception {
        // TODO Auto-generated method stub

    }

}
