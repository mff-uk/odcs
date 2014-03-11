package cz.cuni.mff.xrg.odcs.rdf.validator;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * RDF Data validator configuration.
 *
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

	/**
	 * Returns true, if report output should be created only if some invalid
	 * data are found, false otherwise.
	 *
	 * @return true if validator should stop when some invalid data are found,
	 *         false otherwise.
	 */
	public boolean canStopExecution() {
		return stopExecution;
	}

	/**
	 * Returns true, if report output should be created only if some invalid
	 * data are found, false otherwise.
	 *
	 * @return true, if report output should be created only if some invalid
	 *         data are found, false otherwise.
	 */
	public boolean hasSometimesOutput() {
		return sometimesOutput;
	}

	public boolean isStopExecution() {
		return stopExecution;
	}

	public void setStopExecution(boolean stopExecution) {
		this.stopExecution = stopExecution;
	}

	public boolean isSometimesOutput() {
		return sometimesOutput;
	}

	public void setSometimesOutput(boolean sometimesOutput) {
		this.sometimesOutput = sometimesOutput;
	}
	
	/**
	 * Returns true, if DPU configuration is valid, false otherwise.
	 *
	 * @return true, if DPU configuration is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		return true;
	}
}
