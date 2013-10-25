package cz.cuni.mff.xrg.odcs.backend.context;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Reconstruct given {@link Context} based on {@link ExecutionContextInfo}
 * and prepare it for usage. 
 * 
 * @author Petyr
 *
 */
class ContextRestorer {
	
	@Autowired
	private DataUnitFactory dataUnitFactory;
	
	/**
	 * Restore data of given context. If there is some data already 
	 * loaded then does not load them again otherwise nothing happen.
	 * 
	 * @param context
	 */
	public void restore(Context context) throws DataUnitException {
		// we can assume that file exist .. as HDD is persistent
		// so only DataUnits leave to load
				
		context.getInputsManager().reload();
		context.getOutputsManager().reload();
	}

}
