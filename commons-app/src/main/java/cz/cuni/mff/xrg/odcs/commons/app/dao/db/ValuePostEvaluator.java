package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

/**
 * Wraps value for DB parameters, which allows deferred evaluation.
 * 
 * @author Jan Vojt
 * @param <T> type of wrapped value
 * @deprecated unused class, will be removed
 */
@Deprecated
public interface ValuePostEvaluator<T> {

	/**
	 * @return the wrapped value
	 */
	public T evaluate();
	
}
