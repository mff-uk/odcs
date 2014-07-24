package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperties;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.FilesDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import eu.unifiedviews.dataunit.DataUnitException;

public class LocalFSFilesDataUnitFactory implements FilesDataUnitFactory {

    @Value(ConfigProperties.GENERAL_WORKINGDIR)
    private String globalWorkingDirectory;
    
    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;
    
    @Override
    public ManageableWritableFilesDataUnit createManageableWritable(String pipelineId, String dataUnitName) {
        try {
            return new LocalFSFilesDataUnit(rdfDataUnitFactory, globalWorkingDirectory, pipelineId, dataUnitName);
        } catch (DataUnitException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
