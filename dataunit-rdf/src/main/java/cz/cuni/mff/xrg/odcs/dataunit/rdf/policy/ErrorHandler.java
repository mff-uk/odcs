package cz.cuni.mff.xrg.odcs.dataunit.rdf.policy;

/**
 * Error handler used to sanitize the problems that may occur during the
 * execution of methods in {@link RDFDataUnit}.
 * 
 * @author Petyr
 */
public interface ErrorHandler {

	/**
	 * Called when connection failed.
	 *
	 * @param attempt
	 * @return True for next attempt.
	 */
	boolean retryConnection(int attempt);

	/**
	 * Called if the problem in data is found. Throw exception in order to
	 * terminate the operation.
	 *
	 * @throws OperationInterupted
	 */
	void invalidData() throws OperationInterupted;

	/**
	 * Called on conflict in data.
	 *
	 * @throws OperationInterupted
	 */
	void parseConflict() throws OperationInterupted;
	
}