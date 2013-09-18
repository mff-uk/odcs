package cz.cuni.xrg.intlib.commons.app.module.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event indicate that certain DPU needs to be reloaded due it's changes.
 * @author Petyr
 *
 */
public class DPURefreshEvent extends ApplicationEvent {

	/**
	 * DPU's relative directory name.
	 */
	private String directoryName;

	public DPURefreshEvent(Object source, String directoryName) {
		super(source);
		this.directoryName = directoryName;
	}

	public String getDirectoryName() {
		return directoryName;
	}
	
}