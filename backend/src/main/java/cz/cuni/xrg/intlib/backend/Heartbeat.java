package cz.cuni.xrg.intlib.backend;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import cz.cuni.xrg.intlib.backend.execution.EngineEvent;
import cz.cuni.xrg.intlib.backend.execution.EngineEventType;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;

public class Heartbeat implements Runnable, ApplicationEventPublisherAware {

	/**
	 * Event publisher used to publicise events.
	 */
	private ApplicationEventPublisher eventPublisher = null;	
	
	/*
	 * Refresh interval. 
	 */
	private int checkInterval = 1000 * 60;
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			// let engine check database
			eventPublisher.publishEvent(new EngineEvent(EngineEventType.CheckDatabase, this));
			// and also the scheduler as well
			eventPublisher.publishEvent(new SchedulerCheckDatabase(this));
			// sleep
			try {
				Thread.sleep(checkInterval);
			} catch (InterruptedException e) {
				// it's time to end 
				break;
			}
		}
	}
	
	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		eventPublisher = applicationEventPublisher;
	}

}
