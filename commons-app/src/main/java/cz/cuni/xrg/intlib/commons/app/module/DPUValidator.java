package cz.cuni.xrg.intlib.commons.app.module;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;

/**
 * Can be used to add additional checks for loaded DPU. The validation class
 * will not be shared by multiple threads.
 * 
 * @author Petyr
 * @see DPUModuleManipulator
 */
public interface DPUValidator {

	/**
	 * Check if {@link DPUTemplateRecord} and it's instance is functional 
	 * and can be used.
	 * @param dpu
	 * @param dpuInstance
	 * @return False if the DPU should not be loaded into application.
	 */
	public boolean validate(DPUTemplateRecord dpu, Object dpuInstance);
	
	/**
	 * Return message that will be shown to the user in case that 
	 * {@link #check(DPUTemplateRecord, Object)} return false.
	 * @return
	 */
	public String getMessage();
}
