package cz.cuni.mff.xrg.odcs.rdf.validator;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * @author Jiri Tomes
 */
public class RDFDataValidatorConfig extends DPUConfigObjectBase {

	private boolean stopExecution;

	private boolean sometimesOutput;

	public RDFDataValidatorConfig() {
		this.stopExecution = false;
		this.sometimesOutput = true;
	}

	public RDFDataValidatorConfig(boolean stopExecution, boolean sometimesOutput) {
		this.stopExecution = stopExecution;
		this.sometimesOutput = sometimesOutput;
	}

	public boolean canStopExecution() {
		return stopExecution;
	}

	public boolean hasSometimesOutput() {
		return sometimesOutput;
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
}
