package cz.cuni.xrg.intlib.commons.app.execution;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 *
 * @author Jan Vojt
 */
public class LogFacade {

	private static final Logger LOG = LoggerFactory.getLogger(PipelineFacade.class);
	
	/**
	 * Entity manager for accessing database with persisted objects
	 */
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Returns all logs in the database.
	 * USE WITH CAUTION, THERE MAY BE MANY!!!
	 * 
	 * @return all log messages in db
	 */
	public List<LogMessage> getAllLogs() {

		@SuppressWarnings("unchecked")
		List<LogMessage> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM LogMessage e").getResultList(),
				LogMessage.class
		);

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
			lvls.add(level.getName());
		}
		
		@SuppressWarnings("unchecked")
		List<LogMessage> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM LogMessage e"
					+ " WHERE e.levelString IN (:lvl)")
					.setParameter("lvl", lvls)
					.getResultList(),
				LogMessage.class
		);
		
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
			lvls.add(level.getName());
		}
		
		@SuppressWarnings("unchecked")
		List<LogMessage> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM LogMessage e"
					+ " WHERE e.levelString IN (:lvl)"
					+ "	AND e.properties[:propKey] = :propVal")
					.setParameter("lvl", lvls)
					.setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
					.setParameter("propVal", Long.toString(exec.getId()))
					.getResultList(),
				LogMessage.class
		);
		
		return resultList;
	}

}
