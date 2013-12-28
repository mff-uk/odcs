package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation providing access to {@link Schedule} data objects.
 *
 * @author Jan Vojt
 * @author Petyr
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbScheduleImpl extends DbAccessBase<Schedule>
							implements DbSchedule {

	public DbScheduleImpl() {
		super(Schedule.class);
	}
	
	@Override
	public List<Schedule> getAllSchedules() {
		JPQLDbQuery<Schedule> jpql = new JPQLDbQuery<>("SELECT e FROM Schedule e");
		return executeList(jpql);
	}

	@Override
	public List<Schedule> getSchedulesFor(Pipeline pipeline) {
		
		JPQLDbQuery<Schedule> jpql = new JPQLDbQuery<>(
				"SELECT e FROM Schedule e WHERE e.pipeline = :pipeline");
		jpql.setParameter("pipeline", pipeline);
		
		return executeList(jpql);
	}

	@Override
	public List<Schedule> getFollowers(Pipeline pipeline, Boolean enabled) {
		
		JPQLDbQuery<Schedule> jpql = new JPQLDbQuery<>();
		String query = "SELECT s FROM Schedule s JOIN s.afterPipelines p"
					+ " WHERE p.id = :pipeline AND s.type = :type";
		
		jpql.setParameter("pipeline", pipeline.getId())
				.setParameter("type", ScheduleType.AFTER_PIPELINE);
		
		if (enabled != null) {
			query += " AND s.enabled = :enabled";
			jpql.setParameter("enabled", enabled);
		}
		
		return executeList(jpql.setQuery(query));
	}

	@Override
	public List<Schedule> getAllTimeBased() {		
		JPQLDbQuery<Schedule> jpql = new JPQLDbQuery<>(
				"SELECT s FROM Schedule s"
				+ " WHERE s.type = :type");
		jpql.setParameter("type", ScheduleType.PERIODICALLY);
		
		return executeList(jpql);
	}
	
	@Override
	public List<Schedule> getActiveRunAfterBased() {
		final String queryStr = "SELECT s FROM Schedule s"
				+ " WHERE s.type = :type"
				+ " AND s.enabled = 1";
				
		JPQLDbQuery<Schedule> jpql = new JPQLDbQuery<>(queryStr);
		jpql.setParameter("type", ScheduleType.AFTER_PIPELINE);
		
		return executeList(jpql);
	}	

	@Override
	public List<Date> getLastExecForRunAfter(Schedule schedule) {
		final String queryStr = "SELECT"
				+ " max(exec.end)"
				+ " FROM Schedule schedule"
				+ " JOIN schedule.afterPipelines pipeline"
				+ " JOIN PipelineExecution exec ON exec.pipeline = pipeline"
				+ " WHERE schedule.id = :schedule AND exec.status IN :status"
				+ " GROUP BY pipeline.id";
		
		Set<PipelineExecutionStatus> statuses = new HashSet<>();
		statuses.add(PipelineExecutionStatus.FINISHED_SUCCESS);
		statuses.add(PipelineExecutionStatus.FINISHED_WARNING);
		
		TypedQuery<Date> tq = em.createQuery(queryStr, Date.class);
		tq.setParameter("schedule", schedule.getId());
		tq.setParameter("status", statuses);
		
		List<Date> resultList = Collections.checkedList(tq.getResultList(), Date.class);
		return resultList;
	}
	
}
