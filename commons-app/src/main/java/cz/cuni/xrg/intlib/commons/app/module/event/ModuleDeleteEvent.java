package cz.cuni.xrg.intlib.commons.app.module.event;

/**
 * Event indicate that DPU from given directory should be uninstalled from 
 * the system.
 * 
 * @author Petyr
 * 
 */
public class ModuleDeleteEvent extends ModuleEvent {

	/**
	 * DPU's relative directory name.
	 */
	private String directoryName;

	public ModuleDeleteEvent(Object source,
			String directoryName) {
		super(source);
		this.directoryName = directoryName;
	}

	public String getDirectoryName() {
		return directoryName;
	}
	
}