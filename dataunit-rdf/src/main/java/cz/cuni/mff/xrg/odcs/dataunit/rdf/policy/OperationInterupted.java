package cz.cuni.mff.xrg.odcs.dataunit.rdf.policy;

/**
 * Report operation interruption on request.
 * 
 * @author Petyr
 */
public class OperationInterupted extends Exception {
	
	public OperationInterupted(String cause) {
		super(cause);
	}
	
}
