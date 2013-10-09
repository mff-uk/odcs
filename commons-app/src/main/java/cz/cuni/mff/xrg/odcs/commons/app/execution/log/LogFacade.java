package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Level;

/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 *
 * @author Jan Vojt
 */
public class LogFacade {

    /**
     * Entity manager for accessing database with persisted objects
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * Returns all logs in the database. USE WITH CAUTION, THERE MAY BE MANY!!!
     *
     * @return all log messages in db
     */
    public List<LogMessage> getAllLogs() {

        @SuppressWarnings("unchecked")
        List<LogMessage> resultList = Collections.checkedList(
                em.createQuery("SELECT e FROM LogMessage e").getResultList(),
                LogMessage.class);

        return resultList;
    }

    /**
     * Returns all log messages of given levels.
     *
     * @param levels set of log message levels
     * @return log messages
     */
    public List<LogMessage> getLogs(Set<Level> levels) {

        // convert levels to strings
        Set<String> lvls = new HashSet<>();
        for (Level level : levels) {
            lvls.add(level.toString());
        }

        @SuppressWarnings("unchecked")
        List<LogMessage> resultList = Collections.checkedList(
                em.createQuery("SELECT e FROM LogMessage e"
                + " WHERE e.levelString IN :lvl")
                .setParameter("lvl", lvls)
                .getResultList(),
                LogMessage.class);

        return resultList;
    }

    /**
     * Returns all log messages for given pipeline execution.
     *
     * @param exec pipeline execution to show logs for
     * @return log messages
     */
    public List<LogMessage> getLogs(PipelineExecution exec) {

        @SuppressWarnings("unchecked")
        List<LogMessage> resultList = Collections.checkedList(
                em.createQuery("SELECT e FROM LogMessage e"
                + " LEFT JOIN e.properties p"
                + " WHERE KEY(p) = :propKey AND p = :propVal")
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(exec.getId()))
                .getResultList(),
                LogMessage.class);

        return resultList;
    }

    /**
     * Returns all log messages of given levels for given pipeline execution.
     *
     * @param exec pipeline execution to show logs for
     * @param levels set of levels to include in result
     * @return log messages
     */
    public List<LogMessage> getLogs(PipelineExecution exec, Set<Level> levels) {

        // convert levels to strings
        Set<String> lvls = new HashSet<>();
        for (Level level : levels) {
            lvls.add(level.toString());
        }

        @SuppressWarnings("unchecked")
        List<LogMessage> resultList = Collections.checkedList(
                em.createQuery("SELECT e FROM LogMessage e"
                + " LEFT JOIN e.properties p"
                + " WHERE e.levelString IN :lvl"
                + "	AND KEY(p) = :propKey AND p = :propVal")
                .setParameter("lvl", lvls)
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(exec.getId()))
                .getResultList(),
                LogMessage.class);

        return resultList;
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

        // convert levels to strings
        Set<String> lvls = new HashSet<>();
        for (Level level : levels) {
            lvls.add(level.toString());
        }

        @SuppressWarnings("unchecked")
        List<LogMessage> resultList = Collections.checkedList(
                em.createQuery("SELECT e FROM LogMessage e"
                + " LEFT JOIN e.properties p"
                + " LEFT JOIN e.properties p2"
                + " WHERE e.levelString IN :lvl"
                + " AND ((KEY(p) = :propKey AND p = :propVal)"
                + "			AND (KEY(p2) = :propKeyDpu AND p2 = :propValDpu))")
                .setParameter("lvl", lvls)
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(exec.getId()))
                .setParameter("propKeyDpu", LogMessage.MDC_DPU_INSTANCE_KEY_NAME)
                .setParameter("propValDpu", Long.toString(dpu.getId()))
                .getResultList(),
                LogMessage.class);

        return resultList;
    }

    /**
     * Fetches exception stacktraces for given log message logged by logback
     * into RDBMS.
     *
     * @param message
     * @return
     */
    public LogException getLogException(LogMessage message) {

        @SuppressWarnings("unchecked")
        List<LogExceptionLine> resultList = Collections.checkedList(
                em.createQuery("SELECT l FROM LogExceptionLine l"
                + " LEFT JOIN l.message m"
                + " WHERE m = :msg"
                + " ORDER BY l.lineIndex ASC")
                .setParameter("msg", message)
                .getResultList(),
                LogExceptionLine.class);

        if (resultList.isEmpty()) {
            return null;
        }

        return new LogException(resultList);
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
        // convert levels to strings
        Set<String> lvls = new HashSet<>();
        for (Level level : levels) {
            lvls.add(level.toString());
        }

        Long count = (Long) em.createQuery(
                "SELECT COUNT(e) FROM LogMessage e"
                + " LEFT JOIN e.properties p"
                + " WHERE e.levelString IN :lvl"
                + " AND KEY(p) = :propKey AND p = :propVal")
                .setParameter("lvl", lvls)
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(exec.getId()))
                .getSingleResult();
        return count > 0;
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

        @SuppressWarnings("unchecked")
        List<LogMessage> resultList = Collections.checkedList(
                em.createQuery("SELECT e FROM LogMessage e"
                + " WHERE e.properties[:propKey] = :propVal"
                + "	AND e.properties[:propKeyDpu] = :propValDpu")
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(exec.getId()))
                .setParameter("propKeyDpu", LogMessage.MDC_DPU_INSTANCE_KEY_NAME)
                .setParameter("propValDpu", Long.toString(dpu.getId()))
                .getResultList(),
                LogMessage.class);

        return resultList;
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
        return em.find(LogMessage.class, id);
    }

    public InputStream getLogsAsStream(PipelineExecution pipelineExecution, DPUInstanceRecord dpu, Level level, String message, String source, Date start, Date end) {
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

            data = getLogs(pipelineExecution,dpu, levels, source, message, start, end);
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
        if(sb.length() == 0) {
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
    private List<LogMessage> getLogs(PipelineExecution pipelineExecution, DPUInstanceRecord dpu, Set<Level> levels, String source, String message, Date start, Date end) {
        List<LogMessage> data;
        @SuppressWarnings("unchecked")
        StringBuilder query = new StringBuilder("SELECT e FROM LogMessage e"
                + " LEFT JOIN e.properties p"
                + " LEFT JOIN e.properties p2"
                + " WHERE e.levelString IN :lvl"
                + " AND (KEY(p) = :propKey AND p = :propVal)");
        if (dpu != null) {
            query.append("AND (KEY(p2) = :propKeyDpu AND p2 = :propValDpu)");
        } else {
            //query.append(')');
        }
        if (source != null) {
            query.append(" AND (e.source LIKE :source)");
        }
        if (message != null) {
            query.append(" AND (e.message LIKE :message)");
        }
        if(start != null) {
            query.append(" AND e.timestamp >= :start");
        }
        if(end != null) {
            query.append(" AND e.timestamp <= :end");
        }
        // convert levels to strings
        Set<String> lvls = new HashSet<>();
        for (Level l : levels) {
            lvls.add(l.toString());
        }
        Query q = em.createQuery(query.toString())
                .setParameter("lvl", lvls)
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(pipelineExecution.getId()));
        if (dpu != null) {
            q.setParameter("propKeyDpu", LogMessage.MDC_DPU_INSTANCE_KEY_NAME)
                    .setParameter("propValDpu", Long.toString(dpu.getId()));
        }
        if (source != null) {
            q.setParameter("source", source);
        }
        if (message != null) {
            q.setParameter("message", message);
        }
        if(start != null) {
            q.setParameter("start", start.getTime());
        }
        if(end != null) {
            q.setParameter("end", end.getTime());
        }
        data = Collections.checkedList(q.getResultList(), LogMessage.class);
        return data;
    }
}
