package cz.cuni.mff.xrg.odcs.backend.context;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Seal context so it can not be further modified by DPU's user accessible 
 * interface.
 * 
 * @author Petyr
 *
 */
class ContextSealer {

	/**
	 * Seal given {@link Context}.
	 * @param context
	 */
	public void seal(Context context) {
		// seal data units
		seal(context.getInputsManager());
		seal(context.getOutputsManager());
	}
	
	/**
	 * Seal data units for given {@link DataUnitManager}'s content.
	 * @param dataUnitManage
	 */
	private void seal(DataUnitManager dataUnitManage) {
		for (ManagableDataUnit dataUnit : dataUnitManage.getDataUnits()) {
			dataUnit.madeReadOnly();
		}
		dataUnitManage.getDataUnits().clear();
	}
	
}
