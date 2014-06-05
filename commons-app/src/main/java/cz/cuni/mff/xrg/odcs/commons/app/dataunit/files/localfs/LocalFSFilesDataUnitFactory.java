package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperties;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.WritableFilesDataUnitFactory;

public class LocalFSFilesDataUnitFactory implements WritableFilesDataUnitFactory {

    @Value(ConfigProperties.GENERAL_WORKINGDIR)
    private String globalWorkingDirectory;
    
    @Override
    public ManageableWritableFilesDataUnit create(String dataUnitName, String dataGraph) {
        return null;
//        return new LocalFSFilesDataUnit(globalWorkingDirectory, dataUnitName);
    }
}
