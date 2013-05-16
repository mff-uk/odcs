package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

/**
 * Factory for execution context.
 * 
 * @author Petyr
 *
 */
public class ExecutionContextFactory {

	/**
	 * Create new context manager and return write access.
	 * @param directory The root directory of context.
	 */
	public ExecutionContextWriter createNew(File directory) {
		return null;
	}
	
	/**
	 * Restore execution context from given directory and return write 
	 * access.
	 * @param directory The root directory of context.
	 * @return
	 */
	public ExecutionContextReader restoreAsWrite(File directory) {
		return null;
	}	
	
	/**
	 * Restore execution context from given directory and return read 
	 * only access.
	 * @param directory The root directory of context.
	 * @return
	 */
	public ExecutionContextReader restoreAsRead(File directory) {
		return null;
	}
	
}
