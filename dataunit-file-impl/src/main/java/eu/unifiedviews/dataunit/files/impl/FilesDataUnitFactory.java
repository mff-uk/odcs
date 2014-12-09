package eu.unifiedviews.dataunit.files.impl;

import java.io.File;

import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Factory for files data units.
 *
 * @author Škoda Petr
 */
public class FilesDataUnitFactory {

    /**
     *
     * @param dataUnitName Name of data unit.
     * @param graphAsString Unique graph URI in a string form.
     * @param connectionSource Connection source for RDF repository.
     * @param workingDirectory Working directory.
     * @return
     * @throws DataUnitException
     */
    public ManageableWritableFilesDataUnit create(String dataUnitName, String graphAsString, ConnectionSource connectionSource, File workingDirectory) throws DataUnitException {
        workingDirectory.mkdirs();
        return new LocalFSFilesDataUnit(dataUnitName, workingDirectory, graphAsString, connectionSource);
    }
}
