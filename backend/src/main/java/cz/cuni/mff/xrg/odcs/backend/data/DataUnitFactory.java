package cz.cuni.mff.xrg.odcs.backend.data;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import eu.unifiedviews.dataunit.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.FilesDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;

/**
 * Create new DataUnits based on given id, name and type in given working directory. The class is suppose to
 * be use as spring bean and it's methods can be run concurrently.
 *
 * @author Petr Škoda
 */
public class DataUnitFactory {

    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;

    @Autowired
    private FilesDataUnitFactory filesDataUnitFactory;

    public DataUnitFactory() {
    }

    /**
     * Create {@link DataUnit} and store information about it into the context.
     *
     * @param type              Requested type of data unit.
     * @param pipelineId        Will be removed!!!
     * @param dataUnitUri       Unique URI identification of data unit.
     * @param dataUnitName      DataUnit's name, can't be changed in future.
     * @param dataUnitDirectory DataUnit's working directory.
     * @return DataUnit
     */
    public ManagableDataUnit create(ManagableDataUnit.Type type,
            String pipelineId,
            String dataUnitUri,
            String dataUnitName,
            File dataUnitDirectory) {
        switch (type) {
            case FILES:
                return filesDataUnitFactory.create(
                        pipelineId, dataUnitName, dataUnitUri, dataUnitDirectory);
            case RDF:
                return rdfDataUnitFactory.create(
                        pipelineId, dataUnitName, dataUnitUri);
            default:
                throw new RuntimeException("Unknown DataUnit type.");
        }
    }
}
