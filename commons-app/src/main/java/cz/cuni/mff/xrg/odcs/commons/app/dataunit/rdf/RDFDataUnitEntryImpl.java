package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

public class RDFDataUnitEntryImpl implements RDFDataUnit.Entry {
    
    private final String symbolicName;

    private final URI dataGraphURI;

    public RDFDataUnitEntryImpl(String symbolicName, URI dataGraphURI) {
        this.symbolicName = symbolicName;
        this.dataGraphURI = dataGraphURI;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public URI getDataGraphURI() throws DataUnitException {
        return dataGraphURI;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[symbolicName=" + symbolicName + ",dataGraphURI=" + String.valueOf(dataGraphURI) + "]";
    }

}
