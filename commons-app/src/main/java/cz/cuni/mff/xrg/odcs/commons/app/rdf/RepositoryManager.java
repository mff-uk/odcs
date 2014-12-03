package cz.cuni.mff.xrg.odcs.commons.app.rdf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.resource.MissingResourceException;
import cz.cuni.mff.xrg.odcs.commons.app.resource.ResourceManager;
import eu.unifiedviews.commons.rdf.repository.ManagableRepository;
import eu.unifiedviews.commons.rdf.repository.RDFException;
import eu.unifiedviews.commons.rdf.repository.RepositoryFactory;

/**
 * Provide access to repositories.
 * 
 * @author Škoda Petr
 */
public class RepositoryManager {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryManager.class);

    /**
     * Locks used to synchronise access to {@link Repository}.
     */
    private final Map<String, Object> locks = new HashMap<>();
    
    /**
     * Repositories.
     */
    private final Map<String, ManagableRepository> repositories = Collections.synchronizedMap(new HashMap<String, ManagableRepository>());

    /**
     * Repository factory.
     */
    private final RepositoryFactory factory = new RepositoryFactory();

    /**
     * Type of used repositories.
     */
    private ManagableRepository.Type repositoryType;

    @Value("${database.rdf.platform}")
    private String repositoryTypeString;

    /**
     * URL of remote repository.
     */
    @Value("${database.rdf.url:}")
    private String url;

    /**
     * User.
     */
    @Value("${database.rdf.user:}")
    private String user;

    /**
     * Password.
     */
    @Value("${database.rdf.password:}")
    private String password;

    @Autowired
    private ResourceManager resourceManager;

    @PostConstruct
    protected void init() {
        // Setup factory.
        try {
            factory.setLocalParameters(resourceManager.getRootRepositoriesDir().toString());
        } catch (MissingResourceException ex){
            throw new RuntimeException(ex);
        }
        factory.setRemoteParameters(url, user, password);
        switch (repositoryTypeString) {
            case "inMemoryRDF":
                repositoryType = ManagableRepository.Type.INMEMORY_RDF;
                break;
            case "localRDF":
                repositoryType = ManagableRepository.Type.LOCAL_RDF;
                break;
            case "virtuoso":
                repositoryType = ManagableRepository.Type.VIRTUOSO;
                break;
            case "remoteRDF":
                repositoryType = ManagableRepository.Type.REMOTE_RDF;
                break;
            default:
                throw new RuntimeException("Unknown repository type.");
        }
    }

    /**
     * Return repository for given execution, if repository doesn't exists then it's created.
     *
     * @param pipelineId
     * @return
     * @throws eu.unifiedviews.commons.rdf.repository.RDFException
     */
    public ManagableRepository get(String pipelineId) throws RDFException {
        synchronized (getLock(pipelineId)) {
            ManagableRepository repository = repositories.get(pipelineId);
            if (repository != null) {
                // Return existing repository.
                return repository;
            }
            // We need to create a new repository.
            repository = factory.create(pipelineId, repositoryType);
            // Add to list and return it.
            repositories.put(pipelineId, repository);
            return repository;
        }
    }

    /**
     * Release repository for given pipeline, if that repository is loaded.
     *
     * @param pipelineId
     * @throws RDFException
     */
    public void release(String pipelineId) throws RDFException {
        synchronized (getLock(pipelineId)) {
            final ManagableRepository repository = repositories.get(pipelineId);
            if (repository != null) {
                repository.release();
            }
        }
    }

    /**
     * Delete repository for given pipeline if the repository is already loaded.
     * 
     * @param pipelineId
     * @throws RDFException
     */
    public void delete(String pipelineId) throws RDFException {
        synchronized (getLock(pipelineId)) {
            final ManagableRepository repository = repositories.get(pipelineId);
            if (repository != null) {
                repository.delete();
            }
        }
    }

    /**
     *
     * @param id
     * @return Lock object for given pipeline.
     */
    private synchronized Object getLock(String pipelineId) {
        if (locks.containsKey(pipelineId)) {
            return locks.get(pipelineId);
        }
        final Object lock = new Object();
        locks.put(pipelineId, lock);
        return lock;
    }

}
