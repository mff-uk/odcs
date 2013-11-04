package cz.cuni.mff.xrg.odcs.frontend.container;

import java.util.Date;

/**
 * Time based size cache.
 * 
 * @author Petyr
 *
 */
final class ValueTimeCache<T> {

	private T value = null;
	
	private Date cacheUpdate = null;
	
	/**
	 * Return cached value or null if the cache does 
	 * not contains valid data. 
	 * @param now
	 * @return
	 */
	public T get(Date now) {
		if (value == null) {
			// we have no cached value
		} else {
			// we have value do time check
			final long elapsed = now.getTime() - cacheUpdate.getTime();
			if (elapsed > 1000) {
				// invalidate because of time
				value = null;
			} 
		}
		return value;
	}
	
	/**
	 * Set cache value and update time stamp.
	 * @param size
	 * @param now
	 */
	public void set(T value, Date now) {
		cacheUpdate = now;
		this.value = value;
	}
	
}
