package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf;

import java.io.File;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;

public class LocalRDFDataUnitFactory implements RDFDataUnitFactory {
    private String repositoryPath;

    @Override
    public ManagableRdfDataUnit create(String pipelineId, String dataUnitName, String dataGraph) {
        return new LocalRDFDataUnit(repositoryPath, pipelineId, dataUnitName, dataGraph);
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    @Override
    public void clean(String pipelineId) {
        try {
            File managerDir = new File(repositoryPath);
            if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
                throw new RuntimeException("Could not create repository manager directory.");
            }
            LocalRepositoryManager localRepositoryManager = RepositoryProvider.getRepositoryManager(managerDir);
            localRepositoryManager.removeRepository(pipelineId);
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new RuntimeException("Could not remove repository", ex);
        }
    }

    @Override
    public void release(String pipelineId) {
        try {
            File managerDir = new File(repositoryPath);
            if (!managerDir.isDirectory() && !managerDir.mkdirs()) {
                throw new RuntimeException("Could not create repository manager directory.");
            }
            LocalRepositoryManager localRepositoryManager = RepositoryProvider.getRepositoryManager(managerDir);
            localRepositoryManager.getRepository(pipelineId).shutDown();;
        } catch (RepositoryConfigException | RepositoryException ex) {
            throw new RuntimeException("Could not remove repository", ex);
        }
    }
    
    
}
