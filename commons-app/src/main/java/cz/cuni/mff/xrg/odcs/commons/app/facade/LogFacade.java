package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.ArrayList;

import ch.qos.logback.classic.Level;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 * 
 * @author Jan Vojt
 */
public interface LogFacade extends Facade {

    /**
     * Return true if there exist logs with given level for given DPU instance
     * of given pipeline execution.
     * 
     * @param exec
     * @param level
     * @return true if logs exist, false otherwise
     */
    boolean existLogsGreaterOrEqual(PipelineExecution exec, Level level);

    /**
     * Return list of all usable log's levels without aggregations. Ordered
     * descending by priority.
     * 
     * @return list of all log levels
     */
    ArrayList<Level> getAllLevels();

}
