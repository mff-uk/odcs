package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import java.util.List;

/**
 * Implementation providing access to {@link Schedule} data objects.
 *
 * @author Jan Vojt
 */
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

}
