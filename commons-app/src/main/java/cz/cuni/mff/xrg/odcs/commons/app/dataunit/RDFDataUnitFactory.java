package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

public interface RDFDataUnitFactory {
    ManagableRdfDataUnit create(String dataUnitName, String dataGraph);
}
