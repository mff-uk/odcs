package cz.cuni.mff.xrg.odcs.backend.scheduling;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineFinished;
import cz.cuni.mff.xrg.odcs.backend.scheduling.event.SchedulerCheckDatabase;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade;

/**
 * Take care about execution of scheduled plans.
 * 
 * @author Petyr
 * 
 */
class Scheduler implements ApplicationListener<ApplicationEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(Schedule.class);
	
	/**
	 * Pipeline facade.
	 */
	@Autowired
	private PipelineFacade pipelineFacade;
	
	/**
	 * Schedule facade.
	 */
	@Autowired
	private ScheduleFacade scheduleFacade;
	
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
		// set related scheduler
		pipelineExec.setSchedule(schedule);
		// will wake up other pipelines on end ..
		pipelineExec.setSilentMode(false);
		// set user .. copy owner of schedule
		pipelineExec.setOwner(schedule.getOwner());

		// save data into DB -> in next DB check Engine start the execution
		pipelineFacade.save(pipelineExec);
		scheduleFacade.save(schedule);
	}
	
	/**
	 * Run pipelines that should be executed after given pipeline.
	 * @param pipelineFinishedEvent
	 */
	private synchronized void onPipelineFinished(PipelineFinished pipelineFinishedEvent) {
		LOG.debug("onPipelineFinished started");
		if (pipelineFinishedEvent.sucess()) {
			// success continue
		} else {
			// execution failed -> ignore
			return;
		}			
		
		if (pipelineFinishedEvent.getExecution().getSilentMode()) {
			// pipeline run in silent mode .. ignore
		} else {
			List<Schedule> toRun = scheduleFacade.getFollowers(
					pipelineFinishedEvent.getExecution().getPipeline());
			// for each .. run
			for (Schedule schedule : toRun) {
				if (schedule.isEnabled()) {
					execute(schedule);
				}
			}
		}
		LOG.debug("onPipelineFinished finished");
	}
	
	/**
	 * Check database for time-based schedules.
	 */
	private synchronized void onTimeCheck() {
		LOG.debug("onTimeCheck started");
		// check DB for pipelines based on time scheduling
		Date now = new Date();
		// get all pipelines that are time based
		List<Schedule> candidates = scheduleFacade.getAllTimeBased();
		// check ..
		for (Schedule schedule : candidates) {
			// we use information about next execution
			Date nextExecution = schedule.getNextExecutionTimeInfo();
			if (nextExecution == null) {
				// do not run .. is disabled, missed it's time  
			} else if (nextExecution.before(now)) {
				LOG.debug("Executing id:{} name: {} time of execution is {}", 
					schedule.getId(), schedule.getName(), 
					nextExecution);
				
				execute(schedule);
			}
		}
		LOG.debug("onTimeCheck finished");
	}	
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if (event instanceof PipelineFinished) {
			PipelineFinished pipelineFinishedEvent = (PipelineFinished) event;
			// ...
			LOG.debug("Recieved PipelineFinished event");
			onPipelineFinished(pipelineFinishedEvent);
		} else if (event instanceof SchedulerCheckDatabase) {
			// ...
			LOG.debug("Recieved SchedulerCheckDatabase event");
			onTimeCheck();
		} else {
			// unknown event .. ignore
		}
	}
	
}
