package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperties;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.FilesDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

public class LocalFSFilesDataUnitFactory implements FilesDataUnitFactory {

    @Value(ConfigProperties.GENERAL_WORKINGDIR)
    private String globalWorkingDirectory;

    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;

    @Override
    public ManageableWritableFilesDataUnit createManageableWritable(String pipelineId, String dataUnitName) {
        try {
            String workingDirectoryURIString = Files.createTempDirectory(FileSystems.getDefault().getPath(globalWorkingDirectory), pipelineId).toFile().toURI().toASCIIString();
            RDFDataUnit backingDataUnit = rdfDataUnitFactory.create(pipelineId, dataUnitName, workingDirectoryURIString);
            return new LocalFSFilesDataUnit(dataUnitName, workingDirectoryURIString, backingDataUnit);
        } catch (DataUnitException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
