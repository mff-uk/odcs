package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files;

public interface WritableFilesDataUnitFactory {
    ManageableWritableFilesDataUnit create(String pipelineId, String dataUnitName);
}
