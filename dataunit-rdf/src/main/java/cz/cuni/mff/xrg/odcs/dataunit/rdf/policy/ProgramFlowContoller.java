package cz.cuni.mff.xrg.odcs.dataunit.rdf.policy;

/**
 * Control the program flow in {@link RDFDataUnit}.
 */
public interface ProgramFlowContoller {

	/**
	 * Return true if the {@link RDFDataUnit} should cancel current operation as
	 * soon as possible.
	 *
	 * @return
	 */
	boolean cancel();

}
