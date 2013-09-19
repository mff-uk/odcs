package cz.cuni.xrg.intlib.commons.app.dpu.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event indicate that certain DPU needs to be reloaded due it's changes.
 * 
 * @author Petyr
 * 
 */
public class DPURefreshEvent extends ApplicationEvent {

	/**
	 * DPU's relative directory name.
	 */
	private String directoryName;

	/**
	 * Name of new DPU's jar file.
	 */
	private String jarName;

	public DPURefreshEvent(Object source,
			String directoryName,
			String jarName) {
		super(source);
		this.directoryName = directoryName;
		this.jarName = jarName;
	}

	public String getDirectoryName() {
		return directoryName;
	}
	
	public String getJarName() {
		return jarName;
	}

}