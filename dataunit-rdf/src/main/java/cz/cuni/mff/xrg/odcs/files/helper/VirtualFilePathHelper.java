package cz.cuni.mff.xrg.odcs.files.helper;

import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;

public interface VirtualFilePathHelper {
    interface VirtualFilePathEntry extends FilesDataUnit.Entry {
        String getVirtualFilePath();
    }
    
}
