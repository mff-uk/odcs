package cz.cuni.mff.xrg.odcs.backend.data;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import eu.unifiedviews.dataunit.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.FilesDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;

/**
 * Create new DataUnits based on given id, name and type in given working
 * directory.
 * The class is suppose to be use as spring bean and it's methods can be run
 * concurrently.
 * 
 * @author Petyr
 */
public class DataUnitFactory {

    /**
     * Application configuration.
     */
    @Autowired
    private AppConfig appConfig;

    //@Value( "${jdbc.url}" ) private String jdbcUrl;

    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;
    
    @Autowired
    private FilesDataUnitFactory filesDataUnitFactory; 

    public DataUnitFactory() {
    }

    /**
     * Create {@link DataUnit} and store information about it into the context.
     * 
     * @param type
     *            Requested type of data unit.
     * @param pipelineId
     * @param id
     *            DataUnit's id assigned by application, must be unique!
     * @param name
     *            DataUnit's name, can't be changed in future.
     * @param directory
     *            DataUnit's working directory.
     * @return DataUnit
     */
    public ManagableDataUnit create(ManagableDataUnit.Type type,
            String pipelineId,
            String id,
            String name,
            File directory) {
        switch (type) {
            case FILES:
                return filesDataUnitFactory.createManageableWritable(pipelineId, name, GraphUrl.translateDataUnitId(id), id);
            case RDF:
                return rdfDataUnitFactory.create(pipelineId, name, GraphUrl.translateDataUnitId(id));
            case FILE:
                // create the DataUnit and return it
                return FileDataUnitFactory.create(name, directory);
            default:
                throw new RuntimeException("Unknown DataUnit type.");
        }
    }
}
