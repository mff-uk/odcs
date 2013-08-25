package cz.cuni.xrg.intlib.backend.scheduling;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFinished;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.scheduling.PeriodUnit;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;

/**
 * Take care about execution of scheduled plans.
 * 
 * @author Petyr
 * 
 */
public class Scheduler implements ApplicationListener {

	/**
	 * Size of max minutes differences for two times that should be considered
	 * to be the same.
	 */
	private static final int MINUTE_TOLERANCE = 50;

	/**
	 * Access to the database
	 */
	@Autowired
	protected DatabaseAccess database;

	/**
	 * Create execution for given schedule. Also if the schedule is runOnce then
	 * disable it. Ignore enable/disable option for schedule.
	 * 
	 * @param schedule
	 */
	private void execute(Schedule schedule) {
		// update schedule
		schedule.setLastExecution(new Date());
		// if the schedule is run one then disable it
		if (schedule.isJustOnce()) {
			schedule.setEnabled(false);
		}
		// create PipelineExecution
		PipelineExecution pipelineExec = new PipelineExecution(
				schedule.getPipeline());
		// will wake up other pipelines on end ..
		pipelineExec.setSilentMode(false);

		// save data into DB -> in next DB check Engine start the execution
		database.getPipeline().save(pipelineExec);
		database.getSchedule().save(schedule);
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if (event instanceof PipelineFinished) {
			PipelineFinished pipelineFinishedEvent = (PipelineFinished) event;
			if (pipelineFinishedEvent.getExecution().getSilentMode()) {
				// pipeline run in silent mode .. ignore
			} else {
				List<Schedule> toRun = database.getSchedule().getFollowers(
						pipelineFinishedEvent.getExecution().getPipeline());
				// for each .. run
				for (Schedule schedule : toRun) {
					if (schedule.isEnabled()) {
						execute(schedule);
					}
				}
			}
		} else if (event instanceof SchedulerCheckDatabase) {
			// check DB for pipelines based on time scheduling
			Date now = new Date();
			// get all pipelines that are time based
			List<Schedule> candidates = database.getSchedule()
					.getAllTimeBased();
			// check ..
			for (Schedule schedule : candidates) {
				// we use information about next execution
				Date nextExecution = schedule.getNextExecutionTimeInfo();
				if (nextExecution == null) {
					// runs after another pipeline .. 
				} else if (nextExecution.before(now)) {					
					execute(schedule);
				}
			}

		} else {
			// unknown event .. ignore
		}
	}
}
