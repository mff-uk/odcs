package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files;

public interface FilesDataUnitFactory {
    ManageableWritableFilesDataUnit createManageableWritable(String pipelineId, String dataUnitName, String dataGraph, String dataUnitId);
}
