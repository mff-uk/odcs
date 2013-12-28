package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.DbSchedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.DbScheduleNotification;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import java.util.*;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Facade providing actions with plan.
 *
 * @author Jan Vojt
 * @author Petyr
 */
@Transactional(readOnly = true)
public class ScheduleFacade {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduleFacade.class);

	@Autowired
	private DbSchedule scheduleDao;

	@Autowired
	private DbScheduleNotification scheduleNotificationDao;

	@Autowired(required = false)
	private AuthenticationContext authCtx;

	@Autowired
	private PipelineFacade pipelineFacade;

	/**
	 * Schedule factory. Explicitly call
	 * {@link #save(cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule)} to
	 * persist created entity.
	 *
	 * @return initialized Schedule
	 */
	public Schedule createSchedule() {
		Schedule sch = new Schedule();
		if (authCtx != null) {
			sch.setOwner(authCtx.getUser());
		}
		return sch;
	}

	/**
	 * Returns list of all Plans currently persisted in database.
	 *
	 * @return Plans list
	 * @deprecated use container with paging instead
	 */
	@Deprecated
	@PostFilter("hasPermission(filterObject,'view')")
	public List<Schedule> getAllSchedules() {
		return scheduleDao.getAllSchedules();
	}

	/**
	 * Fetches all {@link Schedule}s planned for given pipeline.
	 *
	 * @param pipeline
	 * @return
	 */
	public List<Schedule> getSchedulesFor(Pipeline pipeline) {
		return scheduleDao.getSchedulesFor(pipeline);
	}

	/**
	 * Fetches all {@link Schedule}s that should be activated after given
	 * pipeline execution.
	 *
	 * @param pipeline pipeline to follow
	 * @param enabled <ul>
	 * <li>if true return only followers with enabled schedules,</li>
	 * <li>if false return only followers with disabled schedules,</li>
	 * <li>if null return all followers.</li></ul>
	 * @return schedules configured to follow given pipeline
	 */
	@Deprecated
	public List<Schedule> getFollowers(Pipeline pipeline, Boolean enabled) {
		return scheduleDao.getFollowers(pipeline, enabled);
	}

	/**
	 * Fetches all schedules configured to follow given pipeline.
	 *
	 * @param pipeline
	 * @return
	 */
	@Deprecated
	public List<Schedule> getFollowers(Pipeline pipeline) {
		return getFollowers(pipeline, (Boolean) null);
	}

	/**
	 * Fetches all {@link Schedule}s which are activated in certain time.
	 *
	 * @return
	 */
	public List<Schedule> getAllTimeBased() {
		return scheduleDao.getAllTimeBased();
	}

	/**
	 * Find Schedule in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	public Schedule getSchedule(long id) {
		return scheduleDao.getInstance(id);
	}

	/**
	 * Saves any modifications made to the Schedule into the database.
	 *
	 * @param schedule
	 */
	@Transactional
	@PreAuthorize("hasPermission(#schedule, 'save')")
	public void save(Schedule schedule) {
		scheduleDao.save(schedule);
	}

	/**
	 * Deletes Schedule from the database.
	 *
	 * @param schedule
	 */
	@Transactional
	public void delete(Schedule schedule) {
		scheduleDao.delete(schedule);
	}

	/**
	 * Deletes notification setting for schedule.
	 *
	 * @param notify notification settings to delete
	 */
	@Transactional
	public void deleteNotification(ScheduleNotificationRecord notify) {
		scheduleNotificationDao.delete(notify);
	}

	/**
	 * Create execution for given schedule. Also if the schedule is runOnce then
	 * disable it. Ignore enable/disable option for schedule.
	 *
	 * @param schedule
	 */
	@Transactional
	public void execute(Schedule schedule) {
		// update schedule
		Date oldLastChedule = schedule.getLastExecution();
		schedule.setLastExecution(new Date());
		// if the schedule is run one then disable it
		if (schedule.isJustOnce()) {
			schedule.setEnabled(false);
		}
		// create PipelineExecution
		PipelineExecution pipelineExec = new PipelineExecution(
				schedule.getPipeline());
		// set related scheduler
		pipelineExec.setSchedule(schedule);
		// will wake up other pipelines on end ..
		pipelineExec.setSilentMode(false);
		// set user .. copy owner of schedule
		pipelineExec.setOwner(schedule.getOwner());

		// save data into DB -> in next DB check Engine start the execution
		pipelineFacade.save(pipelineExec);

		LOG.debug("Last schedule {} - > {} , new exec id: {}",
				oldLastChedule, schedule.getLastExecution(),
				pipelineExec.getId());

		save(schedule);
	}

	/**
	 * Check for all schedule that run after some execution and run them if all
	 * the the pre-runs has been executed. The call of this function may be
	 * expensive as it check for all runAfter based pipelines.
	 *
	 */
	@Transactional
	public void executeFollowers() {
		List<Schedule> toRun = scheduleDao.getActiveRunAfterBased();
		// filter those that should not run
		toRun = filterActiveRunAfter(toRun);
		// and execute
		for (Schedule schedule : toRun) {
			execute(schedule);
		}
	}

	/**
	 * Executes all pipelines scheduled to follow given pipeline.
	 *
	 * @param pipeline to follow
	 */
	@Transactional
	public void executeFollowers(Pipeline pipeline) {
		List<Schedule> toRun = scheduleDao.getFollowers(pipeline, true);
		// filter those that should not run
		toRun = filterActiveRunAfter(toRun);
		// and execute
		for (Schedule schedule : toRun) {
			execute(schedule);
		}
	}
	
	/**
	 * Return schedules that are of run after type and that should be executed
	 * as all after-pipelines already has already been executed.
	 *
	 * @return
	 */
	List<Schedule> filterActiveRunAfter(List<Schedule> candidates) {
		List<Schedule> result = new LinkedList<>();

		for (Schedule schedule : candidates) {
			List<Date> times = scheduleDao.getLastExecForRunAfter(schedule);

			if (times.size() != schedule.getAfterPipelines().size()) {
				// we have wrong number of time, we do not
				// run this execution
				continue;
			}

			boolean execute = true;
			if (schedule.getLastExecution() == null) {
				// the schedule has never run, so just check that no valu is null
				for (Date item : times) {
					if (item == null) {
						// no sucesfull execution so far .. 
						execute = false;
						break;
					}
				}
			} else {
				// the shecule ran, we need not null and also that 
				// the after-pipelines run after schedule's last run
				for (Date item : times) {
					if (item == null) {
						// no sucesfull execution so far .. 
						execute = false;
						break;
					}
					if (item.before(schedule.getLastExecution())) {
						// was executed before, but not atfer 
						execute = false;
						break;
					}
				}
			}

			if (execute) {
				// add to the result list
				result.add(schedule);
			} else {
				// no execution
			}
		}

		return result;
	}

}
