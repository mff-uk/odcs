package cz.cuni.mff.xrg.odcs.commons.app.dataunit.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import eu.unifiedviews.commons.relational.repository.ManagableRelationalRepository;
import eu.unifiedviews.commons.relational.repository.RelationalException;
import eu.unifiedviews.commons.relational.repository.RelationalRepositoryFactory;
import eu.unifiedviews.dataunit.DataUnitException;

public class RelationalRepositoryManager {

    /**
     * Locks used to synchronize access to {@link Repository}.
     */
    private final Map<Long, Object> locks = new HashMap<>();

    private final Map<Long, ManagableRelationalRepository> repositories = Collections.synchronizedMap(new HashMap<Long, ManagableRelationalRepository>());

    private final RelationalRepositoryFactory factory = new RelationalRepositoryFactory();

    private static final Logger LOG = LoggerFactory.getLogger(RelationalRepositoryManager.class);

    private ManagableRelationalRepository.Type repositoryType;

    @Value("${database.dataunit.sql.type:}")
    private String repositoryTypeString;

    /**
     * URL of remote repository.
     */
    @Value("${database.dataunit.sql.baseurl:}")
    private String baseUrl;

    /**
     * User.
     */
    @Value("${database.dataunit.sql.user:}")
    private String user;

    /**
     * Password.
     */
    @Value("${database.dataunit.sql.password:}")
    private String password;

    @Value("${database.dataunit.sql.driver:}")
    private String jdbcDriverName;

    @PostConstruct
    public void init() {
        this.factory.setDatabaseParameters(this.user, this.password, this.baseUrl, this.jdbcDriverName);
        switch (this.repositoryTypeString) {
            case "inMemory":
                this.repositoryType = ManagableRelationalRepository.Type.IN_MEMORY;
                break;
            default:
                LOG.info("Unknown repository type, using default in memory database");
                this.repositoryType = ManagableRelationalRepository.Type.IN_MEMORY;
        }
    }

    /**
     * Get internal dataunit relational database repository for given pipeline
     * 
     * @param executionId
     * @return
     * @throws RelationalException
     * @throws DataUnitException
     */
    public ManagableRelationalRepository getRepository(long executionId) throws RelationalException, DataUnitException {
        synchronized (getLock(executionId)) {
            ManagableRelationalRepository repository = this.repositories.get(executionId);
            if (repository != null) {
                return repository;
            }
            // TODO: if other than in memory database, here the new database should be created
            repository = this.factory.create(executionId, this.repositoryType);
            this.repositories.put(executionId, repository);

            return repository;
        }
    }

    /**
     * Release repository for given pipeline, if that repository is loaded.
     *
     * @param executionId
     * @throws Exception
     */
    public void release(Long executionId) throws Exception {
        synchronized (getLock(executionId)) {
            final ManagableRelationalRepository repository = this.repositories.get(executionId);
            if (repository != null) {
                repository.release();
            }
            // Remove from list.
            this.repositories.remove(executionId);
        }
    }

    /**
     * @param executionId
     * @return Lock object for given pipeline.
     */
    private synchronized Object getLock(Long executionId) {
        if (this.locks.containsKey(executionId)) {
            return this.locks.get(executionId);
        }
        final Object lock = new Object();
        this.locks.put(executionId, lock);
        return lock;
    }
}
