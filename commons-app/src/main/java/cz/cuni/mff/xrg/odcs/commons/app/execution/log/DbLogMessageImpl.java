package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.util.*;
import org.apache.log4j.Level;

/**
 * Implementation providing access to {@link LogMessage} data objects.
 *
 * @author Jan Vojt
 */
public class DbLogMessageImpl extends DbAccessBase<LogMessage>
		implements DbLogMessage {

	public DbLogMessageImpl() {
		super(LogMessage.class);
	}
	
	@Override
	public List<LogMessage> getLogs(PipelineExecution execution,
									DPUInstanceRecord dpu,
									Set<Level> levels,
									String source,
									String message,
									Date start,
									Date end) {
		
		JPQLDbQuery<LogMessage> jpql = new JPQLDbQuery<>();
		StringBuilder select = new StringBuilder("SELECT e FROM LogMessage e");
		StringBuilder where = new StringBuilder();
		
		if (execution != null) {
			select.append(" LEFT JOIN e.properties p");
			where.append(" AND (KEY(p) = :propKey AND p = :propVal)");
			jpql.setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
					.setParameter("propVal", Long.toString(execution.getId()));
		}
		if (dpu != null) {
			select.append(" LEFT JOIN e.properties p2");
			where.append(" AND (KEY(p2) = :propKeyDpu AND p2 = :propValDpu)");
			jpql.setParameter("propKeyDpu", LogMessage.MDC_DPU_INSTANCE_KEY_NAME)
					.setParameter("propValDpu", Long.toString(dpu.getId()));
		}
		if (source != null) {
			where.append(" AND (e.source LIKE :source)");
			jpql.setParameter("source", source);
		}
		if (message != null) {
			where.append(" AND (e.message LIKE :message)");
			jpql.setParameter("message", message);
		}
		if (start != null) {
			where.append(" AND e.timestamp >= :start");
			jpql.setParameter("start", start.getTime());
		}
		if (end != null) {
			where.append(" AND e.timestamp <= :end");
			jpql.setParameter("end", end.getTime());
		}
		if (levels != null) {
			// levels must be given as Strings
			where.append(" AND e.levelString IN :lvl");
			jpql.setParameter("lvl", objectsToStrings(levels));
		}		
		
		String query = where.length() > 0
				? select.toString() + where.replace(1, 4, "WHERE")
				: select.toString();
		
		return executeList(jpql.setQuery(query));
	}

	@Override
	public Long getLogsCount(PipelineExecution exec, Set<Level> levels) {
		
		JPQLDbQuery<LogMessage> jpql = new JPQLDbQuery<>(
				"SELECT COUNT(e) FROM LogMessage e"
				+ " LEFT JOIN e.properties p"
				+ " WHERE e.levelString IN :lvl"
				+ " AND KEY(p) = :propKey AND p = :propVal");
		
		jpql.setParameter("lvl", objectsToStrings(levels))
                .setParameter("propKey", LogMessage.MDPU_EXECUTION_KEY_NAME)
                .setParameter("propVal", Long.toString(exec.getId()));
		
		return executeSize(jpql);
	}
	
	/**
	 * Helper function for converting collection of objects to collection of
	 * strings using {@link Object#toString()} method.
	 * 
	 * @param col collection of objects
	 * @return collection of strings
	 */
	private static Collection<String> objectsToStrings(Collection col) {
		ArrayList<String> result = new ArrayList<>(col.size());
		for (Object item : col) {
			result.add(col.toString());
		}
		return result;
	}

}
