package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import java.util.Date;
import java.util.List;

/**
 * Interface providing access to {@link Schedule} data objects.
 *
 * @author Jan Vojt
 */
public interface DbSchedule extends DbAccess<Schedule> {
	
	/**
	 * Returns list of all Plans currently persisted in database.
	 * 
	 * @return list of scheduled jobs
	 * @deprecated may be slow for many schedules, use paging instead
	 */
	@Deprecated
	public List<Schedule> getAllSchedules();
	
	/**
	 * Fetches all {@link Schedule}s planned for given pipeline.
	 *
	 * @param pipeline
	 * @return
	 */
	public List<Schedule> getSchedulesFor(Pipeline pipeline);
	
	/**
	 * Fetches all {@link Schedule}s that should be activated after given
	 * pipeline execution.
	 *
	 * @param pipeline pipeline to follow
	 * @param enabled <ul>
	 *		<li>if true return only followers with enabled schedules,</li>
	 *		<li>if false return only followers with disabled schedules,</li>
	 *		<li>if null return all followers.</li></ul>
	 * @return schedules configured to follow given pipeline
	 */
	public List<Schedule> getFollowers(Pipeline pipeline, Boolean enabled);
	
	/**
	 * Fetches all {@link Schedule}s which are activated in
	 * certain time.
	 *
	 * @return list of schedules
	 */	
	public List<Schedule> getAllTimeBased();
	
	/**
	 * Fetches active (enabled) {@link Schedule}s which are activated based on 
	 * pipelines executions.
	 * 
	 * @return 
	 */
	public List<Schedule> getActiveRunAfterBased();
	
	/**
	 * Return times of last executions (or null if there has been no successful 
	 * execution) of run-after pipelines for runAfter base schedule.
	 * @param schedule
	 * @return 
	 */
	public List<Date> getLastExecForRunAfter(Schedule schedule);

}
