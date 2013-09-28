package cz.cuni.mff.xrg.odcs.commons.app.module.event;

/**
 * Event indicate that certain DPU needs to be reloaded due it's changes.
 * 
 * @author Petyr
 * 
 */
public class ModuleUpdateEvent extends ModuleEvent {
	
	private String jarName;
	
	public ModuleUpdateEvent(Object source,
			String directoryName,
			String jarName) {
		super(source, directoryName);
		this.jarName = jarName;
	}

	public String getJarName() {
		return jarName;
	}
	
}