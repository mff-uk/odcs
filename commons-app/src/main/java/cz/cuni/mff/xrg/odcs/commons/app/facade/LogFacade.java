package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.io.InputStream;

import java.util.*;

import ch.qos.logback.classic.Level;


/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 *
 * @author Jan Vojt
 */
public interface LogFacade extends Facade {


	/**
	 * Return true if there exist logs with given level for given dpu instance
	 * of given pipeline execution.
	 *
	 * @param exec
	 * @param level
	 * @return
	 */
	boolean existLogsGreaterOrEqual(PipelineExecution exec, Level level);

	/**
	 * Return list of all usable log's levels without aggregations. Ordered
	 * descending by priority.
	 * @return 
	 */
	ArrayList<Level> getAllLevels();
	
	InputStream getLogsAsStream(List<Object> filters);
	
}
