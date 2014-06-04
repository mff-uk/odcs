package cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist;

public interface WritableFileListDataUnitFactory {
    ManageableWritableFileListDataUnit create(String pipelineId, String dataUnitName);
}
