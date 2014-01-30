package cz.cuni.mff.xrg.odcs.dataunit.rdf.policy;

/**
 * Interface for program flow control.
 * 
 * @author Petyr
 */
public interface FlowController {
	
	/**
	 * @return True if the current operation should be cancelled asap.
	 */
	boolean isCancelled();
	
}
