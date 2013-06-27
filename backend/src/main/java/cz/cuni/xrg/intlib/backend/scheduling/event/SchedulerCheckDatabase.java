package cz.cuni.xrg.intlib.backend.scheduling.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event for {@link cz.cuni.xrg.intlib.backend.scheduling.Scheduler}. Anounce him to check
 * database and start time planed execution.
 * 
 * @author Petyr
 *
 */
public class SchedulerCheckDatabase extends ApplicationEvent {

	public SchedulerCheckDatabase(Object source) {
		super(source);
	}

}
