package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperties;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.FilesDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

public class LocalFSFilesDataUnitFactory implements FilesDataUnitFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFSFilesDataUnitFactory.class);

    @Value(ConfigProperties.GENERAL_WORKINGDIR)
    private String globalWorkingDirectory;

    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;

    @Override
    public ManageableWritableFilesDataUnit createManageableWritable(String pipelineId, String dataUnitName, String dataGraph, String dataUnitId) {
        try {
            File workingFile = new File(globalWorkingDirectory, dataUnitId);
            workingFile.mkdirs();
            String workingDirectoryURIString = workingFile.toURI().toASCIIString();
            RDFDataUnit backingDataUnit = rdfDataUnitFactory.create(pipelineId, dataUnitName, dataGraph);
            return new LocalFSFilesDataUnit(dataUnitName, workingDirectoryURIString, backingDataUnit, dataGraph);
        } catch (DataUnitException ex) {
            LOG.error("Can't create file data unit.", ex);
        }
        return null;
    }
}
