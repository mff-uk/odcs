package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.inmemory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;

public class InMemoryRDFDataUnitFactory implements RDFDataUnitFactory {

    private String repositoryPath;

    @Override
    public ManagableRdfDataUnit create(String pipelineId, String dataUnitName, String dataGraph) {
        return new InMemoryRDFDataUnit(repositoryPath, dataUnitName, dataGraph);
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    @Override
    public void clean(String pipelineId) {
        // no-op
    }

    @Override
    public void release(String pipelineId) {
        // no-op
    }
    
}
