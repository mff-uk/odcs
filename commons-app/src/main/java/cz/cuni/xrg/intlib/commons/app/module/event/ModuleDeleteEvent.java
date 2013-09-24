package cz.cuni.xrg.intlib.commons.app.module.event;

/**
 * Event indicate that DPU from given directory should be uninstalled from 
 * the system.
 * 
 * @author Petyr
 * 
 */
public class ModuleDeleteEvent extends ModuleEvent {

	public ModuleDeleteEvent(Object source,
			String directoryName) {
		super(source, directoryName);
	}
	
}