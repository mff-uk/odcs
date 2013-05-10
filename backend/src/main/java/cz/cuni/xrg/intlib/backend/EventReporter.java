package cz.cuni.xrg.intlib.backend;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Print all spring event to the standard output.
 * @author Petyr
 *
 */
public class EventReporter implements ApplicationListener {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.println("Event: " + event.toString());
	}

}
