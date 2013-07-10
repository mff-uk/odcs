package cz.cuni.xrg.intlib.backend.scheduling;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFinished;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;

/**
 * Take care about execution of scheduled plans.
 * 
 * @author Petyr
 *
 */
public class Scheduler implements ApplicationListener {

	/**
	 * Access to the database
	 */
	protected DatabaseAccess database;	
	
	public Scheduler(DatabaseAccess database) {
    	this.database = database;
    }
	
	/**
	 * Create execution for given schedule. Also
	 * if the schedule is runOnce then disable it.
	 * Ignore enable/disable option for schedule.
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
		PipelineExecution pipelineExec =  new PipelineExecution(schedule.getPipeline());
		// will wake up other pipelines on end .. 
		pipelineExec.setSilentMode(false);
		
		// save data into DB -> in next DB check Engine start the execution
		database.getPipeline().save(pipelineExec);
		database.getSchedule().save(schedule);
	}
		
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
	
		
		if (event instanceof PipelineFinished) {
			PipelineFinished pipelineFinishedEvent = (PipelineFinished)event;
			if (pipelineFinishedEvent.getExecution().getSilentMode()) {
				// pipeline run in silent mode .. ignore
			} else {
				List<Schedule> toRun = 
						database.getSchedule().getFollowers(
								pipelineFinishedEvent.getExecution().getPipeline());
				// for each .. run 
				for (Schedule schedule : toRun) {
					if (schedule.isEnabled()) {
						execute(schedule);
					}
				}
			}			
		} else if (event instanceof SchedulerCheckDatabase) {
			List<Schedule> all = database.getSchedule().getAllSchedules();
			
			// check DB for pipelines based on time scheduling
			Date now = new Date();
			// get all pipelines that are time based
			List<Schedule> candidates = database.getSchedule().getAllTimeBased();
			// check .. 
			for (Schedule schedule : candidates) {
				if (schedule.getLastExecution() == null) {
					// use first execution to determine when run pipeline
					// run as soon as possible
					if (schedule.getFirstExecution().before(now)) {
						execute(schedule);
					}
				} else if (TimeScheduleHelper.runExecution(schedule.getFirstExecution(), schedule.getLastExecution(), 
						now, schedule.getPeriod(), schedule.getPeriodUnit()) ) {					
					// time elapsed -> execute
					execute(schedule);
				}
			}
			
		} else {
			// unknown event .. ignore
		}		
	}
}
