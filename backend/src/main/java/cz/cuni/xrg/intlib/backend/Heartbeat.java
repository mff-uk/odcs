package cz.cuni.xrg.intlib.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.xrg.intlib.backend.execution.event.EngineEvent;
import cz.cuni.xrg.intlib.backend.execution.event.EngineEventType;
import cz.cuni.xrg.intlib.backend.scheduling.event.SchedulerCheckDatabase;

/**
 * Class periodically emits events.
 * 
 * @author Petyr
 *
 */
public class Heartbeat implements Runnable {

	/**
	 * Logger class.
	 */
	private static Logger LOG = LoggerFactory.getLogger(Heartbeat.class);
	
	/**
	 * Interval for events publishing. 
	 */
	private static final int CHECK_INTERVAL = 1000 * 30;
	
	/**
	 * Thread for heartbeat.
	 */
	private Thread heartbeatThread = null;
	
	/**
	 * Event publisher used to publish events.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublisher = null;	
	
	/**
	 * Start new thread with {@link Heartbeat} as a daemon.
	 */
	public void start() {
		if (heartbeatThread == null) {
			heartbeatThread = new Thread(this);
			heartbeatThread.setDaemon(true);
			heartbeatThread.start();
		} else {
			LOG.info("Required second start of Heartbeat thread, ignored.");
		}
	}
	
	/**
	 * If exist stop the {@link Heartbeat}'s thread.
	 */
	public void stop() {
		if (heartbeatThread != null) {
			heartbeatThread.interrupt();
		}
	}
	
	@Override
	public void run() {
		LOG.info("Heartbeat is running ... ");
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

}
