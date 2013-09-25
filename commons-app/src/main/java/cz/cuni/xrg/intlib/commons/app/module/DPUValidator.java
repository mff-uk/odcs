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
	 * Check if {@link DPUTemplateRecord} and it's instance is functional and
	 * can be used. If the DPU is invalid then should throw exception
	 * with message for user.
	 * 
	 * @param dpu
	 * @param dpuInstance
	 * @throws DPUValidatorException
	 */
	public void validate(DPUTemplateRecord dpu, Object dpuInstance)
			throws DPUValidatorException;

}
