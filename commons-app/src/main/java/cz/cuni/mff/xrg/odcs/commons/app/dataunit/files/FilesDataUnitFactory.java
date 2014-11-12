package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files;

import java.io.File;

public interface FilesDataUnitFactory {
    
    ManageableWritableFilesDataUnit create(String pipelineId, String dataUnitName, String dataGraph, File directory);

}
