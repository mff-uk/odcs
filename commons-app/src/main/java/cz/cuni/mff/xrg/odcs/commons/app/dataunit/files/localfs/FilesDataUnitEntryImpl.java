package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import eu.unifiedviews.dataunit.files.FilesDataUnit;

public class FilesDataUnitEntryImpl implements FilesDataUnit.Entry {
    private String symbolicName;

    private String fileURIString;

    public FilesDataUnitEntryImpl(String symbolicName, String fileURIString) {
        this.symbolicName = symbolicName;
        this.fileURIString = fileURIString;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public String getFileURIString() {
        return fileURIString;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[symbolicName=" + symbolicName + ",filesystemURI=" + fileURIString + "]";
    }
}
