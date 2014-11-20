package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.remoterdf;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;

public class RemoteRDFDataUnitFactory implements RDFDataUnitFactory {

    private String url;

    private String user;

    private String password;

    @Override
    public ManagableRdfDataUnit create(String pipelineId, String dataUnitName, String dataGraph) {
        return new RemoteRDFDataUnit(url, user, password, pipelineId, dataUnitName, dataGraph);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void clean(String pipelineId) {
        try {
            RepositoryManager repositoryManager = RepositoryProvider.getRepositoryManager(url);
            if (repositoryManager instanceof RemoteRepositoryManager) {
                if (user != null && !user.isEmpty()) {
                    ((RemoteRepositoryManager) repositoryManager).setUsernameAndPassword(user, password);
                }
            }
            repositoryManager.removeRepository(pipelineId);
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new RuntimeException("Could not remove repository", ex);
        }
    }

    @Override
    public void release(String pipelineId) {
        try {
            RepositoryManager repositoryManager = RepositoryProvider.getRepositoryManager(url);
            if (repositoryManager instanceof RemoteRepositoryManager) {
                if (user != null && !user.isEmpty()) {
                    ((RemoteRepositoryManager) repositoryManager).setUsernameAndPassword(user, password);
                }
            }
            repositoryManager.getRepository(pipelineId).shutDown();
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new RuntimeException("Could not remove repository", ex);
        }
    }

}
