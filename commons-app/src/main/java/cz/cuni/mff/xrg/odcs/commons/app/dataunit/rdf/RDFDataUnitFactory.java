package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;


public interface RDFDataUnitFactory {
    ManagableRdfDataUnit create(String dataUnitName, String dataGraph);
}
