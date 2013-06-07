package cz.cuni.xrg.intlib.backend.scheduling;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFinished;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;

/**
 * Take care about execution of scheduled plans.
 * 
 * @author Petyr
 *
 */
public class Scheduler implements ApplicationListener {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof PipelineFinished) {
			
		} else if (event instanceof SchedulerCheckDatabase) {
			
		} else {
			// unknown event .. ignore
		}		
	}
}
