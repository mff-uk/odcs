package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.net.URI;

import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;

public class FilesDataUnitEntryImpl implements FilesDataUnitEntry {
    private String symbolicName;

    private URI filesystemURI;

    public FilesDataUnitEntryImpl(String symbolicName, URI filesystemURI) {
        this.symbolicName = symbolicName;
        this.filesystemURI = filesystemURI;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public URI getFilesystemURI() {
        return filesystemURI;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[symbolicName=" + symbolicName + ",filesystemURI=" + filesystemURI.toASCIIString() + "]";
    }
}
