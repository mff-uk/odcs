package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;

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
