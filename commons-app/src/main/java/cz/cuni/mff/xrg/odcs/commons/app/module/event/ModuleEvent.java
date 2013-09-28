package cz.cuni.mff.xrg.odcs.commons.app.module.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for modules related events.
 * @author Petyr
 *
 */
public abstract class ModuleEvent extends ApplicationEvent {

	/**
	 * DPU's relative directory name.
	 */
	private String directoryName;	
	
	public ModuleEvent(Object source, String directoryName) {
		super(source);
		this.directoryName = directoryName;
	}

	public String getDirectoryName() {
		return directoryName;
	}	
	
}
