package cz.cuni.mff.xrg.odcs.rdf.validator;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * @author Jiri Tomes
 */
public class RDFDataValidatorConfig extends DPUConfigObjectBase {

	public boolean stopExecution = false;

	public boolean sometimesOutput = true;

	@Override
	public boolean isValid() {
		return true;
	}
}
