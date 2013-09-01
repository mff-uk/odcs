package cz.cuni.xrg.intlib.backend;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import cz.cuni.xrg.intlib.backend.execution.event.EngineEvent;
import cz.cuni.xrg.intlib.backend.execution.event.EngineEventType;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;

/**
 * Class periodically emits events.
 * 
 * @author Petyr
 *
 */
public class Heartbeat implements Runnable, ApplicationEventPublisherAware {

	/**
	 * Event publisher used to publicise events.
	 */
	private ApplicationEventPublisher eventPublisher = null;	
	
	/**
	 * Interval for events publishing. 
	 */
	private static int CHECK_INTERVAL = 1000 * 30;
	
	@Override
	public void run() {
		// we start with sleeping, so the engine can perform startup checks,
		// before we publish first event
		while(!Thread.interrupted()) {
			// sleep
			try {
				Thread.sleep(CHECK_INTERVAL);
			} catch (InterruptedException e) {
				// it's time to end 
				break;
			}
			
			// let engine check database
			eventPublisher.publishEvent(new EngineEvent(EngineEventType.CHECK_DATABASE, this));
			// and the scheduler as well
			eventPublisher.publishEvent(new SchedulerCheckDatabase(this));			
		}
	}
	
	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		eventPublisher = applicationEventPublisher;
	}

}
