package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 *
 * @author Petyr
 */
public interface SqlAppender extends Appender<ILoggingEvent> {
	
	/**
	 * Store the data in cache into sql database. Is called one per three 
	 * seconds.
	 */
	void fetch();
	
}
