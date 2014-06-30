package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;

public class FilesDataUnitEntryImpl implements FilesDataUnit.FilesDataUnitEntry {
    private String symbolicName;

    private String filesystemURI;

    public FilesDataUnitEntryImpl(String symbolicName, String filesystemURI) {
        this.symbolicName = symbolicName;
        this.filesystemURI = filesystemURI;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public String getFilesystemURI() {
        return filesystemURI;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[symbolicName=" + symbolicName + ",filesystemURI=" + filesystemURI + "]";
    }
}
