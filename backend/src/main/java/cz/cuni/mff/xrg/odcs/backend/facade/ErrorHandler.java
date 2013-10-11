package cz.cuni.mff.xrg.odcs.backend.facade;

/**
 * Interface for error handlers handling behavior when exceptions are caught
 * while performing certain operation.
 * 
 * <p>
 * <code>ErrorHandler</code>s do not manage retries. Therefore if we want to
 * retry upon failure, we should call the original operation in a loop.
 * <code>ErrorHandler</code>s only decide what to do after given number of
 * failed attempts to perform certain operation.
 *
 * @author Jan Vojt
 */
public interface ErrorHandler {
	
	public <E extends RuntimeException> void handle(int attempt, E ex) throws E;

}
