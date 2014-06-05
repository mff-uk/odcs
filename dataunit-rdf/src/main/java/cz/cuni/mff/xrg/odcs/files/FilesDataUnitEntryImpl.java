package cz.cuni.mff.xrg.odcs.files;

import java.net.URI;

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

}
