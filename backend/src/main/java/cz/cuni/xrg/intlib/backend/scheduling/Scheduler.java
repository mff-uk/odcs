package cz.cuni.xrg.intlib.backend.scheduling;

import java.util.List;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFinished;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;
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
		
	}
	
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof PipelineFinished) {
			PipelineFinished pipelineFinishedEvent = (PipelineFinished)event;
			if (pipelineFinishedEvent.getExecution().getSilentMode()) {
				// pipeline run in silent mode .. ignore
			} else {
				List<Schedule> toRun = database.getPlan().getFollowers(pipelineFinishedEvent.getExecution().getPipeline());
				// for each .. run 
				for (Schedule schedule : toRun) {
					if (schedule.isEnable()) {
						execute(schedule);
					}
				}
			}			
		} else if (event instanceof SchedulerCheckDatabase) {
			
			
		} else {
			// unknown event .. ignore
		}		
	}
}
