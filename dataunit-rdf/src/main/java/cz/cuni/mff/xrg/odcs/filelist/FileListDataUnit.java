package cz.cuni.mff.xrg.odcs.filelist;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.rdf.RDFData;

public interface FileListDataUnit extends DataUnit, Iterable<FileListDataUnit.FileListDataUnitEntry> {
    interface FileListDataUnitEntry {
        String getSymbolicName();

        String getCanonicalPath();
    }
    
    /**
     * Get all RDF data regarding this data unit.
     * @return
     */
    RDFData getRDFData();
}
