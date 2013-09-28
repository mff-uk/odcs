package cz.cuni.mff.xrg.odcs.commons.app.module;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;

/**
 * Enables mark given DPU as updated. Such DPU will be updated
 * in backend before next use.
 * 
 * @author Petyr
 *
 */
public interface ModuleChangeNotifier {

	/**
	 * Mark given DPU as updated. So the given {@link DPUTemplateRecord}
	 * should contains new jar-file name.
	 * @param dpu
	 */
	public void updated(DPUTemplateRecord dpu);
	
	/**
	 * Mark given DPU as new. This says that the {@link DPUTemplateRecord} is
	 * new and it should be loaded from database into application.
	 * @param dpu
	 */
	public void created(DPUTemplateRecord dpu);
	
	/**
	 * Notify listeners that given DPU should be unloaded from system. 
	 * @param dpu
	 */
	public void deleted(DPUTemplateRecord dpu);
	
}
