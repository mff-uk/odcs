package cz.cuni.xrg.intlib.commons.app.module;

/**
 * Inform that required DPU instance is being replaced and 
 * so it's inaccessible.
 * 
 * @author Petyr
 *
 */
public class DpuLockedException extends ModuleException {

	public DpuLockedException() {
		super("DPU is being replaced.");
	}
	
}
