package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.*;

import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 *
 * @author Jan Vojt
 */
@Transactional(readOnly = true)
public class LogFacade {
	
	@Autowired
	private DbLogMessage logDao;
	
	@Autowired
	private DbLogExceptionLine exceptionDao;

    /**
     * Returns all log messages of given levels.
     *
     * @param levels set of log message levels
     * @return log messages
     */
    public List<LogMessage> getLogs(Set<Level> levels) {
		return logDao.getLogs(null, null, levels, null, null, null, null);
    }

    /**
     * Returns all log messages for given pipeline execution.
     *
     * @param exec pipeline execution to show logs for
     * @return log messages
     */
    public List<LogMessage> getLogs(PipelineExecution exec) {
		return logDao.getLogs(exec, null, null, null, null, null, null);
    }

    /**
     * Returns all log messages of given levels for given pipeline execution.
     *
     * @param exec pipeline execution to show logs for
     * @param levels set of levels to include in result
     * @return log messages
     */
    public List<LogMessage> getLogs(PipelineExecution exec, Set<Level> levels) {
		return logDao.getLogs(exec, null, levels, null, null, null, null);
    }

    /**
     * Returns all log messages of given levels for given dpu instance of given
     * pipeline execution.
     *
     * @param exec pipeline execution to show logs for
     * @param dpu	dpu instance to show logs for
     * @param levels set of levels to include in result
     * @return log messages
     */
    public List<LogMessage> getLogs(PipelineExecution exec, DPUInstanceRecord dpu, Set<Level> levels) {
		return logDao.getLogs(exec, dpu, levels, null, null, null, null);
    }

    /**
     * Fetches exception stacktraces for given log message logged by logback
     * into RDBMS.
     *
     * @param message
     * @return
     */
    public LogException getLogException(LogMessage message) {
		return exceptionDao.getLogException(message);
    }

    /**
     * Return true if there exist logs with given level for given dpu instance
     * of given pipeline execution.
     *
     * @param exec
     * @param levels
     * @return
     */
    public boolean existLogs(PipelineExecution exec, Set<Level> levels) {
		return logDao.getLogsCount(exec, levels)>0;
    }

    /**
     * Returns all log messages for given dpu instance of given pipeline
     * execution.
     *
     * @param exec pipeline execution to show logs for
     * @param dpu	dpu instance to show logs for
     * @return log messages
     */
    public List<LogMessage> getLogs(PipelineExecution exec, DPUInstanceRecord dpu) {
		return logDao.getLogs(exec, dpu, null, null, null, null, null);
    }

    /**
     * Returns all {@link Level}s of same or higher priority than given level.
     *
     * @param level level with lowest priority in return list
     * @return List of {@link Level}s with same or higher priority than given
     * level.
     *
     */
    public Set<Level> getLevels(Level level) {
        Set<Level> levels = new HashSet<>();
        LinkedHashSet<Level> allLevels = getAllLevels(true);
        for (Level l : allLevels) {
            if (l.isGreaterOrEqual(level)) {
                levels.add(l);
            }
        }
        return levels;
    }

    /**
     * Returns all levels of log massages, ordered by priority.
     *
     * @param includeAggregates True for including Level.ALL and Level.OFF in
     * result, false otherwise
     * @return LinkedHashSet of all {@link Level}s for log messages, ordered by
     * priority.
     */
    public LinkedHashSet<Level> getAllLevels(boolean includeAggregates) {

        LinkedHashSet<Level> levels = new LinkedHashSet<>(8);
        if (includeAggregates) {
            levels.add(Level.ALL);
            levels.add(Level.OFF);
        }
        levels.add(Level.TRACE);
        levels.add(Level.DEBUG);
        levels.add(Level.INFO);
        levels.add(Level.WARN);
        levels.add(Level.ERROR);
        levels.add(Level.FATAL);

        return levels;
    }

    /**
     * Find {@link LogMessage} in database by ID and return it.
     *
     * @param id Id of log message to find.
     * @return Found log message.
     */
    public LogMessage getLog(long id) {
		return logDao.getInstance(id);
    }

    public InputStream getLogsAsStream(
			PipelineExecution pipelineExecution,
			DPUInstanceRecord dpu,
			Level level,
			String message,
			String source,
			Date start,
			Date end) {
	
		StringBuilder sb = new StringBuilder();

		List<LogMessage> data;
		Set<Level> levels = getLevels(level);
		if (message.isEmpty()) {
			message = null;
		} else {
			message = "**" + message;
		}
		if (source.isEmpty()) {
			source = null;
		} else {
			source = "**" + source;
		}

		if (message == null && source == null && start == null && end == null) {
			if (dpu == null) {
				data = getLogs(pipelineExecution, levels);
			} else {
				data = getLogs(pipelineExecution, dpu, levels);
			}
		} else {

			data = getLogs(pipelineExecution, dpu, levels, source, message,
					start, end);
		}

		for (LogMessage log : data) {
			//17:42:17.661 [http-bio-8084-exec-21] DEBUG v.ConfigurableDataSource - Creating new JDBC DriverManager Connection to [jdbc:virtuoso://localhost:1111/charset=UTF-8]
			sb.append(log.getDate());
			sb.append(' ');
			sb.append(log.getThread());
			sb.append(' ');
			sb.append(log.getLevelString());
			sb.append(' ');
			sb.append(log.getSource());
			sb.append(' ');
			sb.append(log.getMessage());
			sb.append('\r');
			sb.append('\n');
		}
		if (sb.length() == 0) {
			return null;
		}
		return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Gets all logs that satisfy given filters.
     * 
     * @param pipelineExecution
     * @param dpu
     * @param levels
     * @param source
     * @param message
     * @param start
     * @param end
     * @return 
     * 
     */
    private List<LogMessage> getLogs(
			PipelineExecution pipelineExecution,
			DPUInstanceRecord dpu,
			Set<Level> levels,
			String source,
			String message,
			Date start,
			Date end) {
		
		return logDao.getLogs(pipelineExecution, dpu, levels, source, message,
				start, end);
    }
}
