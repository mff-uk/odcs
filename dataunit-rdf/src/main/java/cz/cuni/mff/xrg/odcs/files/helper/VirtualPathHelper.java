package cz.cuni.mff.xrg.odcs.files.helper;

import eu.unifiedviews.dataunit.DataUnitException;

public interface VirtualPathHelper {
    public static final String VIRTUAL_PATH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/files/virtualPath";

    //    interface Entry extends FilesDataUnit.Entry {
//        String getVirtualFilePath();
//    }
//    
//    interface Iteration extends FilesDataUnit.Iteration {
//        public VirtualPathHelper.Entry next() throws DataUnitException;
//    }
//    
//    VirtualPathHelper.Iteration getFiles() throws DataUnitException;
//    
    String getVirtualPath(String symbolicName) throws DataUnitException;

    void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException;
}
