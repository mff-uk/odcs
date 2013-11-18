package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;

/**
 * Interface providing access to {@link LogMessage} data objects.
 *
 * @author Jan Vojt
 */
public interface DbLogMessage extends DbAccess<LogMessage> {
	
	/**
	 * Gets all logs that satisfy given filters.
	 *
	 * @param execution
	 * @param dpu
	 * @param levels
	 * @param source
	 * @param message
	 * @param start
	 * @param end
	 * @return
	 *
	 */
	public List<LogMessage> getLogs(PipelineExecution execution,
									DPUInstanceRecord dpu,
									Set<Level> levels,
									String source,
									String message,
									Date start,
									Date end);

	/**
	 * Count number of logs matching given criteria. Used for paging.
	 * 
	 * @param exec pipeline execution that generated logs
	 * @param levels count logs of given levels
	 * @return number of logs matching given criteria
	 */
	public Long getLogsCount(PipelineExecution exec, Set<Level> levels);
}
