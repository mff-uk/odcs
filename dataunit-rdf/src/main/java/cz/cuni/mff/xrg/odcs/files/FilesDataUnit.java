package cz.cuni.mff.xrg.odcs.files;

import java.net.URI;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.RDFData;

public interface FilesDataUnit extends DataUnit {
    interface FilesDataUnitEntry {
        String getSymbolicName();

        URI getFilesystemURI();
    }
    
    interface FilesIteration extends CloseableIteration<FilesDataUnit.FilesDataUnitEntry, DataUnitException> {
        
    }
    
    /**
     * Get all RDF data regarding this data unit.
     * @return
     */
    RDFData getRDFData();
    
    FilesDataUnit.FilesIteration getFiles() throws DataUnitException;
}
