package cz.cuni.xrg.intlib.commons.app.module.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for modules related events.
 * @author Petyr
 *
 */
public abstract class ModuleEvent extends ApplicationEvent {

	public ModuleEvent(Object source) {
		super(source);
	}

}
