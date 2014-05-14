package cz.cuni.mff.xrg.odcs.filelist;

import java.net.URI;

import info.aduna.iteration.CloseableIteration;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.RDFData;

public interface FileListDataUnit extends DataUnit {
    interface FileListDataUnitEntry {
        String getSymbolicName();

        URI getFilesystemURI();
    }
    
    interface FileListIteration extends CloseableIteration<FileListDataUnit.FileListDataUnitEntry, DataUnitException> {
        
    }
    
    /**
     * Get all RDF data regarding this data unit.
     * @return
     */
    RDFData getRDFData();
    
    FileListDataUnit.FileListIteration getFileList() throws DataUnitException;
}
