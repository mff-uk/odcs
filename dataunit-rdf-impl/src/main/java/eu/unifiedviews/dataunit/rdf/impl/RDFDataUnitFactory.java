package eu.unifiedviews.dataunit.rdf.impl;

import java.io.File;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Factory for RDF data units.
 *
 * @author Škoda Petr
 */
public class RDFDataUnitFactory {

    /**
     *
     * @param dataUnitName Name of data unit.
     * @param graphAsString Unique graph URI in a string form.
     * @param connectionSource Connection source for RDF repository.
     * @param workingDirectory Working directory.
     * @return
     * @throws DataUnitException
     */
    public RDFDataUnitImpl create(String dataUnitName, String graphAsString, ConnectionSource connectionSource, File workingDirectory) throws DataUnitException {
        return new RDFDataUnitImpl(dataUnitName, graphAsString, connectionSource);
    }

}
