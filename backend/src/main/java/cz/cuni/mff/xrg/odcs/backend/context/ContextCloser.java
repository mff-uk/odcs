package cz.cuni.mff.xrg.odcs.backend.context;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Close and save the {@link Context} does not delete the data so
 * {@link Context} can be reconstructed later.
 * 
 * @author Petyr
 * 
 */
class ContextCloser {

	/**
	 * Closet the given context. The context should not be
	 * called after is closed by this method.
	 * 
	 * @param context
	 */
	public void close(Context context) {
		// release data
		release(context.getInputsManager());
		release(context.getOutputsManager());
		
		// we do not delete any directories or files
	}

	/**
	 * Release {@link ManagableDataUnit} from given {@link DataUnitManager} and
	 * delete record about them.
	 * 
	 * @param dataUnitManage
	 */
	private void release(DataUnitManager dataUnitManage) {
		for (ManagableDataUnit dataUnit : dataUnitManage.getDataUnits()) {
			dataUnit.release();
		}
		dataUnitManage.getDataUnits().clear();
	}

}
