package eu.unifiedviews.commons.relational.repository;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationalRepositoryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalRepositoryFactory.class);

    private String baseDbUrl;

    private String userName;

    private String password;

    private String jdbcDriverName;

    public void setDatabaseParameters(String userName, String password, String baseDbUrl, String jdbcDriverName) {
        this.baseDbUrl = baseDbUrl;
        this.userName = userName;
        this.password = password;
        this.jdbcDriverName = jdbcDriverName;
    }

    public ManagableRelationalRepository create(long executionId, ManagableRelationalRepository.Type type) throws RelationalException {
        ManagableRelationalRepository repository = null;
        switch (type) {
            case IN_MEMORY:
                repository = new InMemoryRelationalDatabase(this.baseDbUrl, this.jdbcDriverName, executionId);
                break;
            default:
                throw new RelationalException("Unsupported dataunit relational database type");

        }

        Connection connection = null;
        try {
            connection = repository.getDatabaseConnection();
        } catch (Exception e) {
            throw new RelationalException("Failed to connect to created repository", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ignore) {
            }
        }

        return repository;

    }

}
