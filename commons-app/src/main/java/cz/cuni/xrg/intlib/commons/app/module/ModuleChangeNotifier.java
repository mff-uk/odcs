package cz.cuni.xrg.intlib.commons.app.module;

import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;

/**
 * Enables mark given DPU as updated. Such DPU will be updated
 * in backend before next use.
 * 
 * @author Petyr
 *
 */
public interface ModuleChangeNotifier {

	/**
	 * Mark given DPU as updated.
	 * @param dpu
	 */
	public void updated(DPURecord dpu);
	
}
