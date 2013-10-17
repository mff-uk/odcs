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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

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
	 * Run pipelines that should be executed after given pipeline.
	 * @param pipelineFinishedEvent
	 */
	private synchronized void onPipelineFinished(PipelineFinished pipelineFinishedEvent) {
		LOG.trace("onPipelineFinished started");
		if (pipelineFinishedEvent.sucess()) {
			// success continue
		} else {
			// execution failed -> ignore
			return;
		}			
		
		if (pipelineFinishedEvent.getExecution().getSilentMode()) {
			// pipeline run in silent mode .. ignore
		} else {
			scheduleFacade.executeFollowers(
					pipelineFinishedEvent.getExecution().getPipeline()
			);
		}
		LOG.trace("onPipelineFinished finished");
	}
	
	/**
	 * Check database for time-based schedules.
	 */
	private synchronized void onTimeCheck() {
		LOG.trace("onTimeCheck started");
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
				scheduleFacade.execute(schedule);
			}
		}
		LOG.trace("onTimeCheck finished");
	}	
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if (event instanceof PipelineFinished) {
			PipelineFinished pipelineFinishedEvent = (PipelineFinished) event;
			// ...
			LOG.trace("Recieved PipelineFinished event");
			onPipelineFinished(pipelineFinishedEvent);
		} else if (event instanceof SchedulerCheckDatabase) {
			// ...
			LOG.trace("Recieved SchedulerCheckDatabase event");
			onTimeCheck();
		} else {
			// unknown event .. ignore
		}
	}
	
}
