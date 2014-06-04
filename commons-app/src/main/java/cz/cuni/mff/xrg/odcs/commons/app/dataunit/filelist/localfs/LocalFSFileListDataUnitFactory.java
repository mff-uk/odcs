package cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.localfs;

import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperties;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.ManageableWritableFileListDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.WritableFileListDataUnitFactory;

public class LocalFSFileListDataUnitFactory implements WritableFileListDataUnitFactory {

    @Value(ConfigProperties.GENERAL_WORKINGDIR)
    private String globalWorkingDirectory;
    
    @Override
    public ManageableWritableFileListDataUnit create(String dataUnitName, String dataGraph) {
        return null;
//        return new LocalFSFileListDataUnit(globalWorkingDirectory, dataUnitName);
    }
}
