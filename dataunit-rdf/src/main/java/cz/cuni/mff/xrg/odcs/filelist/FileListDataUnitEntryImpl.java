package cz.cuni.mff.xrg.odcs.filelist;

import java.net.URI;

import cz.cuni.mff.xrg.odcs.filelist.FileListDataUnit.FileListDataUnitEntry;

public class FileListDataUnitEntryImpl implements FileListDataUnitEntry {
    private String symbolicName;

    private URI filesystemURI;

    public FileListDataUnitEntryImpl(String symbolicName, URI filesystemURI) {
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
