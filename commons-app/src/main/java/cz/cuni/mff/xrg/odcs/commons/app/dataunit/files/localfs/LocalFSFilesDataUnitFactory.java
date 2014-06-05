package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperties;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.WritableFilesDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;

public class LocalFSFilesDataUnitFactory implements WritableFilesDataUnitFactory {

    @Value(ConfigProperties.GENERAL_WORKINGDIR)
    private String globalWorkingDirectory;
    
    @Override
    public ManageableWritableFilesDataUnit create(String pipelineId, String dataUnitName) {
        try {
            return new LocalFSFilesDataUnit(globalWorkingDirectory, pipelineId, dataUnitName);
        } catch (DataUnitCreateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
