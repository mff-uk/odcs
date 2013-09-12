package cz.cuni.xrg.intlib.backend.module.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event indicate that certain DPU needs to be reloaded due it's changes.
 * @author Petyr
 *
 */
public class DPURefreshEvent extends ApplicationEvent {

	/**
	 * DPU's relative path.
	 */
	private String relativePath;

	public DPURefreshEvent(Object source, String relativePath) {
		super(source);
		this.relativePath = relativePath;
	}

	public String getRelativePath() {
		return relativePath;
	}
	
}
