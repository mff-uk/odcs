package cz.cuni.xrg.intlib.commons.dpu;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 * Base interface for general DPU.
 * 
 * @author Petyr
 * 
 */
public interface DPU {

	/**
	 * Execute the DPU.
	 * 
	 * @param context DPU's context.
	 * @throws DPUException
	 * @throws DataUnitException
	 * @throws InterruptedException
	 */
	public void execute(DPUContext context)
			throws DPUException,
				DataUnitException,
				InterruptedException;

}
