package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import java.io.File;

import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.commons.rdf.repository.RDFException;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Can be used to build data units.
 *
 * @author Škoda Petr
 */
public interface DataUnitFactory {

    /**
     *
     * @param type Type of data unit to create.
     * @param pipelineId Pipeline id.
     * @param dataUnitUri URI of given DataUnit.
     * @param dataUnitName Name of dataUnit (given by DPU's DataUnit annotation).
     * @param dataUnitDirectory DataUnit's working directory.
     * @return
     * @throws RDFException
     * @throws DataUnitException
     */
    ManagableDataUnit create(ManagableDataUnit.Type type, String pipelineId, String dataUnitUri, String dataUnitName, File dataUnitDirectory) throws RDFException, DataUnitException;

}
