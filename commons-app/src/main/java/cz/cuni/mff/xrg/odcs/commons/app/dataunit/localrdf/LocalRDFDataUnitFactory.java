package cz.cuni.mff.xrg.odcs.commons.app.dataunit.localrdf;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.RDFDataUnitFactory;

public class LocalRDFDataUnitFactory implements RDFDataUnitFactory {
    private String repositoryPath;

    @Override
    public ManagableRdfDataUnit create(String dataUnitName, String dataGraph) {
        return new LocalRDFDataUnit(repositoryPath, dataUnitName, dataGraph);
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }
}
